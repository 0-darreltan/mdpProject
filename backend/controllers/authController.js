const User = require("../models/User");
const bcrypt = require("bcryptjs");
const crypto = require("crypto");
const nodemailer = require("nodemailer");

const register = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({ message: "Semua field harus diisi!" });
    }

    let user = await User.findOne({ email });
    if (user) {
      return res.status(400).json({ message: "Email sudah terdaftar!" });
    }

    if (password.length < 6) {
      return res
        .status(400)
        .json({ message: "Password harus minimal 6 karakter!" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = await User.create({
      name,
      email,
      password: hashedPassword,
    });

    return res
      .status(201)
      .json({ success: true, message: "Registrasi berhasil!" });
  } catch (err) {
    console.error(err.message);
    return res.status(500).json({ message: "Server Error saat registrasi" });
  }
};

const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ message: "Semua field harus diisi!" });
    }

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(400).json({ message: "Email atau password salah!" });
    }

    if (!user.password) {
      return res.status(400).json({ message: "Email atau password salah!" });
    }

    const cekPass = await bcrypt.compare(password, user.password);
    if (!cekPass) {
      return res.status(400).json({ message: "Email atau password salah!" });
    }

    const token = user.generateAuthToken();

    return res.status(200).json({
      success: true,
      message: "Login sukses!",
      token,
        user: {
        id: user._id,
        name: user.name,
        email: user.email,
        profile_picture: user.profile_picture || "",
        role: user.role || "user",
      },
    });
  } catch (err) {
    console.error(err.message);
    return res.status(500).json({ message: "Server Error saat login" });
  }
};

const loginWithGoogle = async (req, res) => {
  try {
    const { name, email, profile_picture } = req.body;

    if (!name || !email) {
      return res.status(400).json({ message: "Semua field harus diisi!" });
    }

    let user = await User.findOne({ email });

    if (!user) {
      const randomPassword = crypto.randomBytes(16).toString("hex");

      const hashedRandomPassword = await bcrypt.hash(randomPassword, 10);

      user = await User.create({
        name,
        email,
        password: hashedRandomPassword,
        auth_provider: "google",
        profile_picture: profile_picture || "",
      });
    } else if (profile_picture && user.profile_picture !== profile_picture) {
      user.profile_picture = profile_picture;
      await user.save();
    }

    const token = user.generateAuthToken();

    return res.status(200).json({
      success: true,
      message: "Login Berhasil",
      token,
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
        profile_picture: user.profile_picture || "",
        role: user.role || "user",
      },
    });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat login dengan Google" });
  }
};

const changePassword = async (req, res) => {
  try {
    const { email, oldPassword, newPassword } = req.body;

    if (!email || !oldPassword || !newPassword) {
      return res.status(400).json({ message: "Semua field harus diisi!" });
    }

    if (newPassword.length < 6) {
      return res.status(400).json({ message: "Password baru harus minimal 6 karakter!" });
    }

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(400).json({ message: "User tidak ditemukan!" });
    }

    if (user.auth_provider === "google") {
      return res.status(400).json({ message: "Akun Google tidak dapat mengubah password!" });
    }

    const isMatch = await bcrypt.compare(oldPassword, user.password);
    if (!isMatch) {
      return res.status(400).json({ message: "Password lama salah!" });
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);
    user.password = hashedPassword;
    await user.save();

    return res.status(200).json({ success: true, message: "Password berhasil diubah!" });
  } catch (err) {
    console.error(err.message);
    return res.status(500).json({ message: "Server Error saat mengubah password" });
  }
};

const forgotPassword = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ message: "Email harus diisi!" });
    }

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({ message: "Email tidak ditemukan!" });
    }

    const resetToken = crypto.randomBytes(20).toString("hex");

    user.resetPasswordToken = resetToken;
    user.resetPasswordExpires = Date.now() + 3600000; 
    await user.save();

    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: process.env.EMAIL_USER, 
        pass: process.env.EMAIL_PASS,  
      },
    });

    const mailOptions = {
      to: user.email,
      from: '"ReTech Support" <jiewandana888@gmail.com>',
      subject: "Reset Password ReTech",
      html: `
        <div style="font-family: Arial, sans-serif; padding: 20px;">
          <h2 style="color: #0F9D58;">Reset Password ReTech</h2>
          <p>Kami menerima permintaan untuk mereset password Anda.</p>
          <p>Silakan gunakan kode token di bawah ini pada aplikasi Anda:</p>
          <div style="background: #f4f4f4; padding: 10px; font-size: 20px; font-weight: bold; text-align: center; border-radius: 5px;">
            ${resetToken}
          </div>
          <p>Token ini akan kadaluarsa dalam waktu 1 jam.</p>
          <p>Jika Anda tidak meminta ini, silakan abaikan email ini.</p>
        </div>
      `
    };

    await transporter.sendMail(mailOptions);

    return res.status(200).json({ 
      success: true, 
      message: "Token reset password telah dikirim ke email Anda." 
    });

  } catch (err) {
  console.error("Error detail:", err);
  return res.status(500).json({ message: "Error: " + err.message }); 
}

};

const resetPassword = async (req, res) => {
  try {
    const { token, newPassword } = req.body;

    if (!token || !newPassword) {
      return res.status(400).json({ message: "Token dan password baru harus diisi!" });
    }

    const user = await User.findOne({
      resetPasswordToken: token,
      resetPasswordExpires: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).json({ message: "Token tidak valid atau sudah kadaluarsa!" });
    }

    if (newPassword.length < 6) {
      return res.status(400).json({ message: "Password baru harus minimal 6 karakter!" });
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);
    user.password = hashedPassword;
    
    user.resetPasswordToken = undefined;
    user.resetPasswordExpires = undefined;
    await user.save();

    return res.status(200).json({ 
      success: true, 
      message: "Password berhasil diperbarui! Silakan login kembali." 
    });

  } catch (err) {
    console.error("Error resetPassword:", err);
    return res.status(500).json({ message: "Server Error saat reset password" });
  }
};

const updateProfilePicture = async (req, res) => {
  try {
    const { email, profile_picture } = req.body;

    if (!email || !profile_picture) {
      return res.status(400).json({ message: "Email dan gambar harus diisi!" });
    }

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({ message: "User tidak ditemukan!" });
    }

    user.profile_picture = profile_picture;
    await user.save();

    return res.status(200).json({
      success: true,
      message: "Profile picture berhasil diupdate!",
      profile_picture: user.profile_picture,
    });
  } catch (err) {
    console.error("Error updateProfilePicture:", err);
    return res.status(500).json({ message: "Server Error saat update gambar" });
  }
};

module.exports = {
  register,
  login,
  loginWithGoogle,
  changePassword,
  forgotPassword,
  resetPassword,
  updateProfilePicture,
};
