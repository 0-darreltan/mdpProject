const express = require("express");
const router = express.Router();
const {
  register,
  login,
  loginWithGoogle,
  changePassword,
  forgotPassword,
  resetPassword,
} = require("../controllers/authController");

router.post("/register", register);
router.post("/login", login);
router.post("/google", loginWithGoogle);
router.post("/change-password", changePassword);
router.post("/forgot-password", forgotPassword);
router.post("/reset-password", resetPassword);

module.exports = router;
