require("dotenv").config();
const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");

const User = require("./models/User");
const DropoffLocation = require("./models/DropoffLocation");

const sampleUsers = [
  {
    name: "Budi",
    email: "budi@gmail.com",
    password: "password123",
  },
  {
    name: "Abdul",
    email: "abdul@gmail.com",
    password: "password123",
  },
];

const sampleLocations = [
  {
    name: "E-Waste Drop Point - Dinas Lingkungan Hidup Surabaya",
    address: "Jl. Menur No.31, Manyar Sabrangan, Kec. Mulyorejo, Surabaya",
    accepted_items: ["Baterai Bekas", "Handphone", "Charger", "Lampu LED"],
    operational_hours: [
      { days: "Senin - Sabtu", time: "08:00 - 15:00" },
      { days: "Minggu", time: "Libur" },
    ],
    latitude: -7.279612,
    longitude: 112.766324,
    // PASANG URL CLOUDINARY DI SINI
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1780480367/menur_tulzul.png",
  },
  {
    name: "ReTech Recycle Center Hub - Gubeng",
    address: "Jl. Raya Gubeng No.45, Gubeng, Kec. Gubeng, Surabaya",
    accepted_items: [
      "Laptop",
      "Komputer/CPU",
      "Keyboard",
      "Mouse",
      "Kabel Data",
    ],
    operational_hours: [
      { days: "Senin - Sabtu", time: "09:00 - 20:00" },
      { days: "Minggu", time: "Libur" },
    ],
    latitude: -7.271123,
    longitude: 112.752411,
    // PASANG URL CLOUDINARY DI SINI
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1780480406/gubeng_mhc989.jpg",
  },
  {
    name: "Drop Box Sampah Elektronik - Kampus Sukolilo",
    address: "Keputih, Kec. Sukolilo, Surabaya (Dekat Halte Bus Kampus)",
    accepted_items: ["Baterai", "Powerbank", "Smartphone Rusak", "Earphone"],
    operational_hours: [
      { days: "Senin - Jumat", time: "07:00 - 21:00" },
      { days: "Sabtu - Minggu", time: "09:00 - 15:00" },
    ],
    latitude: -7.282345,
    longitude: 112.794123,
    // PASANG URL CLOUDINARY DI SINI
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1780480370/sukolilo_uqtqys.jpg",
  },
];

// FUNGSI UTAMA SEEDER
const seedData = async () => {
  try {
    // 1. Hubungkan ke Database MongoDB
    await mongoose.connect(process.env.MONGO_URI);
    console.log("Database connected for seeding...");

    // 2. Bersihkan data lama agar tidak duplikat saat script dijalankan ulang
    await User.deleteMany();
    await DropoffLocation.deleteMany();
    console.log("Old data cleared from Users and Locations.");

    // 3. Proses Hashing Password untuk User sebelum di-insert
    const encryptedUsers = await Promise.all(
      sampleUsers.map(async (user) => {
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(user.password, salt);
        return {
          ...user,
          password: hashedPassword,
        };
      }),
    );

    // 4. Masukkan data User ke MongoDB
    await User.insertMany(encryptedUsers);
    console.log("Sample users seeded successfully!");

    // 5. Masukkan data Lokasi ke MongoDB
    await DropoffLocation.insertMany(sampleLocations);
    console.log("Sample locations (Array of Objects) seeded successfully!");

    // 6. Selesai dan Putuskan Koneksi
    mongoose.connection.close();
    console.log("Database connection closed. Seeding Done!");
    process.exit(0);
  } catch (err) {
    console.error("Error during seeding:", err.message);
    process.exit(1);
  }
};

seedData();
