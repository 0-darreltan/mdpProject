const express = require("express");
const cors = require("cors");
const { db, initDb } = require("./db");

const app = express();
app.use(cors());
app.use(express.json());

// buat get ===============================
app.get("/api/user", (req, res) => {
  db.query("SELECT * FROM user ORDER BY id", (err, results) => {
    if (err) {
      console.error("Error GET /api/user:", err.message);
      return res.status(500).json({ error: err.message });
    }
    res.json(results);
  });
});

app.get("/api/room", (req, res) => {
  db.query("SELECT * FROM room ORDER BY id DESC", (err, results) => {
    if (err) {
      console.error("Error GET /api/room:", err.message);
      return res.status(500).json({ error: err.message });
    }
    res.json(results);
  });
});

app.get("/api/report", (req, res) => {
  const query = `
    SELECT r.id, r.roomid, r.userid, r.laporan, r.status, u.username, rm.namaruang, rm.gedung
    FROM report r
    JOIN user u ON r.userid = u.id
    JOIN room rm ON r.roomid = rm.id
    ORDER BY r.id DESC
  `;
  db.query(query, (err, results) => {
    if (err) {
      console.error("Error GET /api/report:", err.message);
      return res.status(500).json({ error: err.message });
    }
    res.json(results);
  });
});

app.get("/api/report/user/:userid", (req, res) => {
  const { userid } = req.params;
  const query = `
    SELECT r.id, r.roomid, r.userid, r.laporan, r.status, u.username, rm.namaruang, rm.gedung
    FROM report r
    JOIN user u ON r.userid = u.id
    JOIN room rm ON r.roomid = rm.id
    WHERE r.userid = ?
    ORDER BY r.id DESC
  `;
  db.query(query, [userid], (err, results) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(results);
  });
});


app.get("/api/chat/:reportid", (req, res) => {
  const { reportid } = req.params;
  const query = `
    SELECT c.id, c.reportid, c.senderid, c.message, c.created_at, u.username
    FROM chat c
    JOIN user u ON c.senderid = u.id
    WHERE c.reportid = ?
    ORDER BY c.created_at ASC
  `;
  db.query(query, [reportid], (err, results) => {
    if (err) {
      console.error("Error GET /api/chat/:reportid:", err.message);
      return res.status(500).json({ error: err.message });
    }
    res.json(results);
  });
});

//===============================
// buat post ===============================

app.post("/api/user", (req, res) => {
  const { username, role } = req.body;
  db.query(
    "INSERT INTO user (username, role) VALUES (?, ?)",  
    [username, role],
    function (err, results) {
      if (err) {
        console.error("Error POST /api/user:", err.message);
        return res.status(500).json({ error: err.message });
      }
      res.json({
        id: results.insertId,
        username,
        role,
      });
    },
  );
});

app.post("/api/room", (req, res) => {
  const { namaruang, gedung, status } = req.body;
  db.query(
    "INSERT INTO room (namaruang, gedung, status) VALUES (?, ?, ?)",
    [namaruang, gedung, status],
    function (err, results) {
      if (err) {
        console.error("Error POST /api/room:", err.message);
        return res.status(500).json({ error: err.message });
      }
      res.json({
        id: results.insertId,
        namaruang,
        gedung,
        status,
      });
    },
  );
});

app.post("/api/report", (req, res) => {
  const { roomid, userid, laporan, status } = req.body;
  db.query(
    "INSERT INTO report (roomid, userid, laporan, status) VALUES (?, ?, ?, ?)",
    [roomid, userid, laporan, status],
    function (err, results) {
      if (err) {
        console.error("Error POST /api/report:", err.message);
        return res.status(500).json({ error: err.message });
      }
      res.json({
        id: results.insertId,
        roomid,
        userid,
        laporan,
        status,
      });
    },
  );
});

app.post("/api/chat", (req, res) => {
  const { reportid, senderid, message } = req.body;
  db.query(
    "INSERT INTO chat (reportid, senderid, message) VALUES (?, ?, ?)",
    [reportid, senderid, message],
    function (err, results) {
      if (err) {
        console.error("Error POST /api/chat:", err.message);
        return res.status(500).json({ error: err.message });
      }
      res.json({
        id: results.insertId,
        reportid,
        senderid,
        message,
      });
    },
  );
});
//===============================

// buat put ===============================
app.put("/api/room/:id", (req, res) => {
  const { id } = req.params;
  const { namaruang, gedung, status } = req.body;
  db.query(
    "UPDATE room SET namaruang = ?, gedung = ?, status = ? WHERE id = ?",
    [namaruang, gedung, status, id],
    function (err, results) {
      if (err) {
        console.error("Error PUT /api/room:", err.message);
        return res.status(500).json({ error: err.message });
      }
      if (results.affectedRows === 0)
        return res.status(404).json({ message: "Data tidak ditemui!" });
      res.json({
        id: Number(id),
        namaruang,
        gedung,
        status,
      });
    },
  );
});

app.put("/api/report/:id", (req, res) => {
  const { id } = req.params;
  const { status } = req.body;
  db.query("UPDATE report SET status = ? WHERE id = ?", [status, id], function (err, results) {
    if (err) {
      console.error("Error PUT /api/report:", err.message);
      return res.status(500).json({ error: err.message });
    }
    if (results.affectedRows === 0)
      return res.status(404).json({ message: "Data tidak ditemui!" });
    res.json({
      id: Number(id),
      status,
    });
  });
});
//===============================

// buat delete ===============================

app.delete("/api/room/:id", (req, res) => {
  const { id } = req.params;
  db.query("DELETE FROM room WHERE id = ?", [id], function (err, results) {
    if (err) {
      console.error("Error DELETE /api/room:", err.message);
      return res.status(500).json({ error: err.message });
    }
    if (results.affectedRows === 0)
      return res.status(404).json({ message: "Data tidak ditemui!" });
    res.json({ message: "Data dihapus!" });
  });
});
//===============================

const PORT = 3000;

initDb()
  .then(() => {
    app.listen(PORT, () => {
      console.log(`Server is running at http://localhost:${PORT}`);
    });
  })
  .catch((err) => {
    console.error("Server error:", err);
  });
