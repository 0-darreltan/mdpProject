const mongoose = require("mongoose");

const DropoffLocationSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  address: {
    type: String,
    required: true,
  },
  accepted_items: [String], // Bisa menyimpan array contoh: ["HP", "Laptop", "Baterai"]
  latitude: {
    type: Number,
    required: true,
  },
  longitude: {
    type: Number,
    required: true,
  },
});

module.exports = mongoose.model("DropoffLocation", DropoffLocationSchema);
