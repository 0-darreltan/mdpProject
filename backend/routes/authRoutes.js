const express = require("express");
const router = express.Router();
const {
  register,
  login,
  loginWithGoogle,
  changePassword,
} = require("../controllers/authController");

router.post("/register", register);
router.post("/login", login);
router.post("/google", loginWithGoogle);
router.post("/change-password", changePassword);

module.exports = router;
