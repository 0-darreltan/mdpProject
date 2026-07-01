require("dotenv").config();
const express = require("express");
const cors = require("cors");
const connectDB = require("./config/db");

const app = express();

connectDB();

app.use(cors());
app.use(express.json({ limit: "10mb" }));

app.use("/api/auth", require("./routes/authRoutes"));
app.use("/api/locations", require("./routes/locationRoutes"));
app.use("/api/guides", require("./routes/guideRoutes"));

app.get("/", (req, res) => {
  res.send("ReTech API is running...");
});

if (process.env.NODE_ENV !== "production") {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`Server is running at http://localhost:${PORT}`);
  });
}

module.exports = app;