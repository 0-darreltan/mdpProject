const express = require("express");
const router = express.Router();
const { getAllLocations, getDetailLocation, addLocation, updateLocation, deleteLocation } = require("../controllers/locationController");

router.get("/", getAllLocations);
router.get("/:id", getDetailLocation);
router.post("/", addLocation);
router.put("/:id", updateLocation);
router.delete("/:id", deleteLocation);

module.exports = router;