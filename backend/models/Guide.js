const mongoose = require("mongoose");

const GuideSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  category: {
    type: String,
    required: true,
  },
  summary: {
    type: String,
    required: true,
  },
  image_url: {
    type: String,
    required: true,
  },
  file_url: {
    type: String,
    required: true,
  },
});

module.exports = mongoose.model("Guide", GuideSchema);
