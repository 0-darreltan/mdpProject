const express = require("express");
const router = express.Router();
const { getAllGuides, addGuide, updateGuide, deleteGuide } = require("../controllers/guideController");

router.get("/", getAllGuides);
router.post("/", addGuide);
router.put("/:id", updateGuide);
router.delete("/:id", deleteGuide);

module.exports = router;
