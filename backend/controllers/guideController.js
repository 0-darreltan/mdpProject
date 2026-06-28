const Guide = require("../models/Guide");

const getAllGuides = async (req, res) => {
  try {
    const guides = await Guide.find();
    return res.status(200).json(guides);
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat mengambil guide" });
  }
};

const addGuide = async (req, res) => {
  try {
    const { name, category, summary, image_url, file_url } = req.body;

    if (!name || !category || !summary || !image_url || !file_url) {
      return res.status(400).json({ message: "Semua field harus diisi!" });
    }

    let guide = await Guide.findOne({ name });
    if (guide) {
      return res.status(400).json({ message: "Guide sudah terdaftar!" });
    }

    const newGuide = await Guide.create({
      name,
      category,
      summary,
      image_url,
      file_url,
    });

    return res
      .status(201)
      .json({ success: true, message: "Guide berhasil ditambahkan!", data: newGuide });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat menambah guide" });
  }
};

const updateGuide = async (req, res) => {
  try {
    const { id } = req.params;
    const { name, category, summary, image_url, file_url } = req.body;

    if (!name || !category || !summary || !image_url || !file_url) {
      return res.status(400).json({ message: "Semua field harus diisi!" });
    }
    
    const updatedGuide = await Guide.findByIdAndUpdate(
      id,
      { name, category, summary, image_url, file_url },
      { new: true }
    );

    if (!updatedGuide) {
      return res.status(404).json({ message: "Guide tidak ditemukan" });
    }

    return res.status(200).json({ success: true, message: "Guide berhasil diupdate!", data: updatedGuide });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat mengupdate guide" });
  }
};

const deleteGuide = async (req, res) => {
  try {
    const { id } = req.params;
    const deletedGuide = await Guide.findByIdAndDelete(id);

    if (!deletedGuide) {
      return res.status(404).json({ message: "Guide tidak ditemukan" });
    }

    return res.status(200).json({ success: true, message: "Guide berhasil dihapus!" });
  } catch (err) {
    console.error(err.message);
    return res
      .status(500)
      .json({ message: "Server Error saat menghapus guide" });
  }
};

module.exports = {
  getAllGuides,
  addGuide,
  updateGuide,
  deleteGuide,
};
