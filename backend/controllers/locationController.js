const DropoffLocation = require("../models/DropoffLocation");

const getAllLocations = async (req, res) => {
  try {
    const locations = await DropoffLocation.find();
    return res.status(200).json(locations);
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat mengambil lokasi" });
  }
};

module.exports = {
  getAllLocations,
};
