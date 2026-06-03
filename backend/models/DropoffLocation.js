const mongoose = require("mongoose");

const operationalSchema = new mongoose.Schema(
  {
    days: { type: String, required: true },
    time: { type: String, required: true },
  },
  { _id: false },
);

const DropoffLocationSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  address: {
    type: String,
    required: true,
  },
  accepted_items: [{ type: String }],
  operational_hours: [operationalSchema],
  latitude: {
    type: Number,
    required: true,
  },
  longitude: {
    type: Number,
    required: true,
  },
  image_url: {
    type: String,
    required: true,
  },
});

module.exports = mongoose.model("DropoffLocation", DropoffLocationSchema);
