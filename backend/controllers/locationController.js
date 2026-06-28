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

const getDetailLocation = async (req, res) => {
  try {
    const { id } = req.params;
    const location = await DropoffLocation.findById(id);

    if (!location) {
      return res.status(404).json({ message: "Lokasi tidak ditemukan" });
    }

    return res.status(200).json(location);
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat mengambil detail lokasi" });
  }
};

const addLocation = async (req, res) => {
  try {
    const { name, address, accepted_items, operational_hours, latitude, longitude, image_url } = req.body;

    if (!name || !address || !latitude || !longitude || !image_url) {
      return res.status(400).json({ message: "Beberapa field wajib harus diisi!" });
    }

    const newLocation = await DropoffLocation.create({
      name, address, accepted_items, operational_hours, latitude, longitude, image_url
    });

    return res.status(201).json({ success: true, message: "Lokasi berhasil ditambahkan!", data: newLocation });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat menambah lokasi" });
  }
};

const updateLocation = async (req, res) => {
  try {
    const { id } = req.params;
    const { name, address, accepted_items, operational_hours, latitude, longitude, image_url } = req.body;

    if (!name || !address || !latitude || !longitude || !image_url) {
      return res.status(400).json({ message: "Beberapa field wajib harus diisi!" });
    }
    
    const updatedLocation = await DropoffLocation.findByIdAndUpdate(
      id,
      { name, address, accepted_items, operational_hours, latitude, longitude, image_url },
      { new: true }
    );

    if (!updatedLocation) {
      return res.status(404).json({ message: "Lokasi tidak ditemukan" });
    }

    return res.status(200).json({ success: true, message: "Lokasi berhasil diupdate", data: updatedLocation });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat mengupdate lokasi" });
  }
};

const deleteLocation = async (req, res) => {
  try {
    const { id } = req.params;
    const deletedLocation = await DropoffLocation.findByIdAndDelete(id);

    if (!deletedLocation) {
      return res.status(404).json({ message: "Lokasi tidak ditemukan" });
    }

    return res.status(200).json({ success: true, message: "Lokasi berhasil dihapus!" });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat menghapus lokasi" });
  }
};

module.exports = {
  getAllLocations,
  getDetailLocation,
  addLocation,
  updateLocation,
  deleteLocation,
};
