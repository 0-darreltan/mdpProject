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
    auth_provider: "manual",
  },
  {
    name: "Abdul",
    email: "abdul@gmail.com",
    password: "password123",
    auth_provider: "manual",
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
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1780480406/gubeng_mhc989.jpg",
  },
  {
    name: "Drop Box Sampah Elektronik - Kampus Sukolilo",
    address: "Keputih, Kec. Sukolilo, Surabaya",
    accepted_items: [
      "Baterai",
      "Powerbank",
      "Smartphone Rusak/Lama",
      "Earphone",
    ],
    operational_hours: [
      { days: "Senin - Jumat", time: "07:00 - 21:00" },
      { days: "Sabtu - Minggu", time: "09:00 - 15:00" },
    ],
    latitude: -7.282345,
    longitude: 112.794123,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1780480370/sukolilo_uqtqys.jpg",
  },
  {
    name: "Urban Republic - Galaxy Mall 3",
    address:
      "Galaxy Mall, Jl. Dr. Ir. H. Soekarno No.3, Mulyorejo, Kec. Mulyorejo, Surabaya, East Java",
    accepted_items: [
      "Kabel",
      "Baterai",
      "Smartphone Rusak/Lama",
      "Powerbank",
      "Adaptor Charger",
    ],
    operational_hours: [{ days: "Senin - Minggu", time: "10:00 - 22:00" }],
    latitude: -7.2767005,
    longitude: 112.7806097,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379778/Urban_GM_wpdk9s.jpg",
  },
  {
    name: "Urban Republic - Tunjungan Plaza 4",
    address:
      "Tunjungan plaza 4, Lantai 3, Kedungdoro, Kec. Tegalsari, Surabaya, East Java",
    accepted_items: [
      "Kabel",
      "Baterai",
      "Smartphone Rusak/Lama",
      "Powerbank",
      "Adaptor Charger",
    ],
    operational_hours: [{ days: "Senin - Minggu", time: "10:00 - 22:00" }],
    latitude: -7.2614307,
    longitude: 112.7385462,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379734/Urban_TP_rqlo7y.webp",
  },
  {
    name: "Urban Republic - Pakuwon City Mall 3",
    address:
      "Pakuwon City Mall 3 Surabaya, Jl. Raya Laguna KJW Putih Tambak No.2 Lantai 2, Unit L2-51, Kejawaan Putih Tamba, Kec. Mulyorejo, Surabaya, East Java",
    accepted_items: [
      "Kabel",
      "Baterai",
      "Smartphone Rusak/Lama",
      "Powerbank",
      "Adaptor Charger",
    ],
    operational_hours: [{ days: "Senin - Minggu", time: "10:00 - 22:00" }],
    latitude: -7.2766592,
    longitude: 112.8061885,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379660/Urban_PCM_yzgdla.jpg",
  },
  {
    name: "Erafone - Megastore Galaxy Mall 3",
    address:
      "Galaxy Mall 3, Jl. Dr. Ir. H. Soekarno No.340 Lt.3 No. 339, Mulyorejo, Kec. Gubeng, Surabaya, East Java",
    accepted_items: ["Kabel", "Baterai", "Smartphone Rusak/Lama"],
    operational_hours: [{ days: "Senin - Minggu", time: "10:00 - 22:00" }],
    latitude: -7.2766902,
    longitude: 112.7803599,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379580/Erafone_GM_d60fw6.jpg",
  },
  {
    name: "Erafone - Plaza Marina Surabaya",
    address:
      "Plaza Marina, Lt. 3, Jl. Margorejo Indah Utara, Sidosermo, Wonocolo, Surabaya, East Java",
    accepted_items: ["Adaptor Charger", "Baterai", "Smartphone Rusak/Lama"],
    operational_hours: [{ days: "Senin - Minggu", time: "09:00 - 22:00" }],
    latitude: -7.3156099,
    longitude: 112.7486026,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379511/Erafone_PlazaMarina_dcnxmj.jpg",
  },
  {
    name: "Balai Kota Surabaya",
    address:
      "Jl. Taman Surya No.1, Ketabang, Kec. Genteng, Surabaya, East Java",
    accepted_items: ["Baterai", "Gadget Kecil", "Lampu"],
    operational_hours: [
      { days: "Senin - Jumat", time: "08:00 - 16:00" },
      { days: "Sabtu - Minggu", time: "Libur" },
    ],
    latitude: -7.2592031,
    longitude: 112.7469525,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379461/BalaiKota_Surabaya_cucks7.webp",
  },
  {
    name: "Dinas Lingkungan Hidup Provinsi Jawa Timur",
    address:
      "Jl. Wisata Menanggal No.38, Dukuh Menanggal, Kec. Gayungan, Surabaya, East Java",
    accepted_items: ["Baterai", "Lampu", "Router/Modem"],
    operational_hours: [
      { days: "Senin - Jumat", time: "07:30 - 16:00" },
      { days: "Sabtu - Minggu", time: "Libur" },
    ],
    latitude: -7.3461881,
    longitude: 112.7342374,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379407/DLH_Surabaya_grmmg8.jpg",
  },
  {
    name: "Bank Sampah Induk Surabaya",
    address:
      "Jl. Raya Menur No.31-A, Manyar Sabrangan, Kec. Mulyorejo, Surabaya, East Java",
    accepted_items: ["Televisi", "Monitor", "Printer", "dll"],
    operational_hours: [
      { days: "Senin - Kamis dan Sabtu", time: "08:00 - 15:00" },
      { days: "Jumat dan Minggu", time: "Libur" },
    ],
    latitude: -7.2782297,
    longitude: 112.7629409,
    image_url:
      "https://res.cloudinary.com/ddcsuysdl/image/upload/v1782379320/images_veyr94.jpg",
  },
];

const seedData = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI);
    console.log("Database connected for seeding...");

    await User.deleteMany();
    await DropoffLocation.deleteMany();
    console.log("Old data cleared from Users and Locations.");

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

    await User.insertMany(encryptedUsers);
    console.log("Sample users seeded successfully!");

    await DropoffLocation.insertMany(sampleLocations);
    console.log("Sample locations (Array of Objects) seeded successfully!");

    mongoose.connection.close();
    console.log("Database connection closed. Seeding Done!");
    process.exit(0);
  } catch (err) {
    console.error("Error during seeding:", err.message);
    process.exit(1);
  }
};

seedData();
