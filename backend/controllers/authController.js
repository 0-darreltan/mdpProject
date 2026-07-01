const User = require("../models/User");
const bcrypt = require("bcryptjs");
const crypto = require("crypto");

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

module.exports = {
  register,
  login,
  loginWithGoogle,
  changePassword,
};
