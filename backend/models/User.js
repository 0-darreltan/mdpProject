const mongoose = require("mongoose");
const jwt = require("jsonwebtoken");

const UserSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  email: {
    type: String,
    required: true,
    unique: true,
  },
  password: {
    type: String,
    required: function () {
      return this.auth_provider === "manual"; // Hanya wajib jika daftar manual via email
    },
  },
  role: {
    type: String,
    default: "user",
    enum: ["user", "admin"],
  },
  auth_provider: {
    type: String,
    default: "manual",
    enum: ["manual", "google"],
  },
  profile_picture: {
    type: String,
    default: "", // Default kosong untuk manual login
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
  resetPasswordToken: String,
  resetPasswordExpires: Date
});

UserSchema.methods.generateAuthToken = function () {
  const secretKey = process.env.JWT_SECRET || "rahasia_retech_2026";

  return jwt.sign({ id: this._id, email: this.email }, secretKey, {
    expiresIn: "7d",
  });
};

module.exports = mongoose.model("User", UserSchema);
