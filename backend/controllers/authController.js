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
      },
    });
  } catch (err) {
    console.error(err.message);
    return res.status(500).json({ message: "Server Error saat login" });
  }
};

const loginWithGoogle = async (req, res) => {
  try {
    const { name, email } = req.body;

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
      });
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
      },
    });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat login dengan Google" });
  }
};

const forgotPassword = async (req, res) => {
  try {
    const { email } = req.body;
    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ message: "Email tidak terdaftar di database!" });
    }

    const resetToken = crypto.randomBytes(20).toString("hex");
    user.resetPasswordToken = resetToken;
    user.resetPasswordExpires = Date.now() + 3600000; 
    await user.save();

    // --- KONFIGURASI GMAIL ---
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: "jiewandana888@gmail.com", 
        pass: "dhpi gmxz hetc zkik",  
      },
    });

    const mailOptions = {
      to: user.email,
      from: '"ReTech Support" <retech@gmail.com>',
      subject: "Reset Password ReTech",
      html: `
        <h3>Reset Password Anda</h3>
        <p>Anda menerima email ini karena ada permintaan untuk reset password akun ReTech Anda.</p>
        <p>Gunakan Token berikut untuk reset password Anda:</p>
        <h2 style="color: #0F9D58;">${resetToken}</h2>
        <p>Token berlaku selama 1 jam.</p>
        <p>Jika Anda tidak meminta ini, abaikan email ini.</p>
      `
    };

    await transporter.sendMail(mailOptions);

    res.status(200).json({ 
      success: true, 
      message: "Berhasil! Cek email Anda (termasuk folder spam) untuk mendapatkan token."
    });

  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Gagal mengirim email reset password" });
  }
};

const resetPassword = async (req, res) => {
  try {
    const { token, newPassword } = req.body;

    // Cari user dengan token yang valid dan belum expired
    const user = await User.findOne({
      resetPasswordToken: token,
      resetPasswordExpires: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).json({ message: "Token tidak valid atau sudah kadaluarsa!" });
    }

    // Hash password baru (sesuai standar bcrypt di backend Anda)
    user.password = await bcrypt.hash(newPassword, 10);

    // Hapus token setelah digunakan
    user.resetPasswordToken = undefined;
    user.resetPasswordExpires = undefined;
    await user.save();

    res.status(200).json({ success: true, message: "Password berhasil diperbarui! Silakan login kembali." });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Server Error saat reset password" });
  }
};

module.exports = {
  register,
  login,
  loginWithGoogle,
  forgotPassword,
  resetPassword,
};
