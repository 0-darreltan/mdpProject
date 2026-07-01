const express = require("express");
const router = express.Router();
const {
  register,
  login,
  loginWithGoogle,
  changePassword,
  forgotPassword,
  resetPassword,
  updateProfilePicture,
} = require("../controllers/authController");

router.post("/register", register);
router.post("/login", login);
router.post("/google", loginWithGoogle);
router.post("/change-password", changePassword);
router.post("/forgot-password", forgotPassword);
router.post("/reset-password", resetPassword);
router.post("/update-profile-picture", updateProfilePicture);

module.exports = router;
