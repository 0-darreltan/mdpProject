const { db, initDb } = require("./db");

const runSeeder = async () => {
  try {
    await initDb();
    const userTable = `CREATE TABLE IF NOT EXISTS user (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), role VARCHAR(255))`;
    const roomTable = `CREATE TABLE IF NOT EXISTS room (id INT AUTO_INCREMENT PRIMARY KEY, namaruang VARCHAR(255), gedung VARCHAR(255), status VARCHAR(255))`;
    const reportTable = `CREATE TABLE IF NOT EXISTS report (id INT AUTO_INCREMENT PRIMARY KEY, roomid INT, userid INT, laporan TEXT, status VARCHAR(255))`;
    const chatTable = `
            CREATE TABLE IF NOT EXISTS chat (
                id INT AUTO_INCREMENT PRIMARY KEY,
                reportid INT,
                senderid INT,
                message TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        `;

    db.query(userTable, (err) => {
      if (err) throw err;
      const userValues = [["admin", "admin"], ["user", "user"], ["wawa", "user"]];
      
      db.query("INSERT INTO user (username, role) VALUES ?", [userValues], (err) => {
        if (err) console.error("Gagal seed user:", err.message);

        db.query(roomTable, (err) => {
          if (err) throw err;
          const roomValues = [["E101", "E", "Aman"], ["L205", "L", "Bermasalah"]];

          db.query("INSERT INTO room (namaruang, gedung, status) VALUES ?", [roomValues], (err) => {
            if (err) console.error("Gagal seed room:", err.message);

            db.query(reportTable, (err) => {
              if (err) throw err;
              
              const reportValues = [[1, 2, "Laporan kerusakan AC", "PENDING"]];

              db.query("INSERT INTO report (roomid, userid, laporan, status) VALUES ?", [reportValues], (err) => {
                if (err) console.error("Gagal seed report:", err.message);

                db.query(chatTable, (err) => {
                  if (err) throw err;

    
                  const chatValues = [
                    [1, 2, "Ini ac gak dingin bos, puanas cik"],
                    [1, 1, "Walawe, nanti tak cek"]
                  ];

                  db.query("INSERT INTO chat (reportid, senderid, message) VALUES ?", [chatValues], (err) => {
                    if (err) console.error("Gagal seed chat:", err.message);
                    else console.log("Semua data berhasil di-seed!");
                    db.end();
                  });
                });
              });
            });
          });
        });
      });
    });

  } catch (error) {
    console.error("Seeder dihentikan karena:", error);
    if (db) db.end();
  }
};

runSeeder();