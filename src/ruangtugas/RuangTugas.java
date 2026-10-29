
package ruangtugas;

import java.awt.*;
import javax.swing.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RuangTugas extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private DefaultListModel<String> taskModel;
    private JList<String> taskList;
    private ArrayList<Integer> taskIds;

    private JTextField namaField;
    private JTextField matkulField;

    private JComboBox<String> tanggalBox;
    private JComboBox<String> bulanBox;
    private JComboBox<String> tahunBox;
    private JComboBox<String> statusBox;

    public RuangTugas() {
        setTitle("RuangTugas - Manajemen Tugas Mahasiswa");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        taskModel = new DefaultListModel<String>();
        taskIds = new ArrayList<Integer>();

        // Sidebar
        JPanel sidebar = new JPanel(
            new GridLayout(5, 1, 5, 5)
        );
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBackground(new Color(45, 48, 75));
        sidebar.setBorder(
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        );

        JLabel logo = new JLabel("RuangTugas");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnDashboard = buatTombol("Dashboard");
        JButton btnDaftar = buatTombol("Daftar Tugas");
        JButton btnTambah = buatTombol("Tambah Tugas");
        JButton btnTentang = buatTombol("Tentang");

        sidebar.add(logo);
        sidebar.add(btnDashboard);
        sidebar.add(btnDaftar);
        sidebar.add(btnTambah);
        sidebar.add(btnTentang);

        // Panel navigasi
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(halamanDashboard(), "dashboard");
        contentPanel.add(halamanDaftar(), "daftar");
        contentPanel.add(halamanTambah(), "tambah");
        contentPanel.add(halamanTentang(), "tentang");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Navigasi halaman
        btnDashboard.addActionListener(e ->
            cardLayout.show(contentPanel, "dashboard")
        );

        btnDaftar.addActionListener(e -> {
            muatTugas();
            cardLayout.show(contentPanel, "daftar");
        });

        btnTambah.addActionListener(e ->
            cardLayout.show(contentPanel, "tambah")
        );

        btnTentang.addActionListener(e ->
            cardLayout.show(contentPanel, "tentang")
        );

        // Muat data dari database saat aplikasi dibuka
        muatTugas();
    }

    // Membuat tombol sidebar
    private JButton buatTombol(String teks) {
        JButton tombol = new JButton(teks);
        tombol.setFocusPainted(false);
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setBackground(new Color(65, 69, 102));
        tombol.setForeground(Color.WHITE);
        tombol.setFont(new Font("Arial", Font.PLAIN, 14));
        return tombol;
    }

    // Halaman Dashboard
    private JPanel halamanDashboard() {
        JPanel panel = new JPanel(
            new BorderLayout(15, 15)
        );
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel judul = new JLabel("Dashboard");
        judul.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel deskripsi = new JLabel(
            "<html>Selamat datang di RuangTugas!<br><br>"
            + "Kelola tugas kuliah dan deadline kamu "
            + "dalam satu tempat.</html>"
        );
        deskripsi.setFont(new Font("Arial", Font.PLAIN, 17));

        JButton tombol = new JButton("Lihat Daftar Tugas");
        tombol.addActionListener(e -> {
            muatTugas();
            cardLayout.show(contentPanel, "daftar");
        });

        JPanel isi = new JPanel(
            new GridLayout(3, 1, 10, 10)
        );
        isi.add(judul);
        isi.add(deskripsi);
        isi.add(tombol);

        panel.add(isi, BorderLayout.NORTH);
        return panel;
    }

    // Halaman Daftar Tugas
    private JPanel halamanDaftar() {
        JPanel panel = new JPanel(
            new BorderLayout(10, 10)
        );
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel judul = new JLabel("Daftar Tugas");
        judul.setFont(new Font("Arial", Font.BOLD, 26));

        taskList = new JList<String>(taskModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton tombolHapus = new JButton("Hapus Tugas");

        tombolHapus.addActionListener(e -> {
            int index = taskList.getSelectedIndex();

            if (index < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pilih tugas yang ingin dihapus."
                );
                return;
            }

            int id = taskIds.get(index);

            int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus tugas ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
            );

            if (konfirmasi == JOptionPane.YES_OPTION) {
                hapusTugas(id);
            }
        });

        panel.add(judul, BorderLayout.NORTH);
        panel.add(
            new JScrollPane(taskList),
            BorderLayout.CENTER
        );
        panel.add(tombolHapus, BorderLayout.SOUTH);

        return panel;
    }

    // Halaman Tambah Tugas
    private JPanel halamanTambah() {
        JPanel panel = new JPanel(
            new BorderLayout(15, 15)
        );
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel judul = new JLabel("Tambah Tugas Baru");
        judul.setFont(new Font("Arial", Font.BOLD, 26));

        JPanel form = new JPanel(
            new GridLayout(5, 2, 10, 15)
        );

        namaField = new JTextField();
        matkulField = new JTextField();

        // Dropdown tanggal, bulan, dan tahun
        tanggalBox = new JComboBox<String>();

        bulanBox = new JComboBox<String>(
            new String[] {
                "Januari", "Februari", "Maret",
                "April", "Mei", "Juni",
                "Juli", "Agustus", "September",
                "Oktober", "November", "Desember"
            }
        );

        tahunBox = new JComboBox<String>();

        for (int tahun = 2026; tahun <= 2035; tahun++) {
            tahunBox.addItem(String.valueOf(tahun));
        }

        bulanBox.setSelectedIndex(0);
        tahunBox.setSelectedItem("2026");

        JPanel panelDeadline = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 5, 0)
        );

        panelDeadline.add(tanggalBox);
        panelDeadline.add(bulanBox);
        panelDeadline.add(tahunBox);

        bulanBox.addActionListener(e -> perbaruiTanggal());
        tahunBox.addActionListener(e -> perbaruiTanggal());

        perbaruiTanggal();

        statusBox = new JComboBox<String>(
            new String[] {
                "Belum Dikerjakan",
                "Sedang Dikerjakan",
                "Selesai"
            }
        );

        form.add(new JLabel("Nama Tugas:"));
        form.add(namaField);

        form.add(new JLabel("Mata Kuliah:"));
        form.add(matkulField);

        form.add(new JLabel("Deadline:"));
        form.add(panelDeadline);

        form.add(new JLabel("Status:"));
        form.add(statusBox);

        JButton tombolSimpan = new JButton("Simpan Tugas");
        JButton tombolBatal = new JButton("Batal");

        form.add(tombolSimpan);
        form.add(tombolBatal);

        // Simpan tugas ke database
        tombolSimpan.addActionListener(e -> {
            String nama = namaField.getText().trim();
            String matkul = matkulField.getText().trim();

            if (nama.isEmpty() || matkul.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nama tugas dan mata kuliah harus diisi."
                );
                return;
            }

            String tanggalTerpilih =
                (String) tanggalBox.getSelectedItem();

            String tahunTerpilih =
                (String) tahunBox.getSelectedItem();

            if (tanggalTerpilih == null
                    || tahunTerpilih == null
                    || bulanBox.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pilih deadline terlebih dahulu."
                );
                return;
            }

            int tanggal = Integer.parseInt(tanggalTerpilih);
            int bulan = bulanBox.getSelectedIndex() + 1;
            int tahun = Integer.parseInt(tahunTerpilih);

            String deadline = String.format(
                "%04d-%02d-%02d",
                tahun, bulan, tanggal
            );

            String status =
                (String) statusBox.getSelectedItem();

            String sql = "INSERT INTO tugas "
                + "(nama_tugas, mata_kuliah, deadline, status) "
                + "VALUES (?, ?, ?, ?)";

            try (Connection conn = Koneksi.getConnection();
                 PreparedStatement ps =
                     conn.prepareStatement(sql)) {

                ps.setString(1, nama);
                ps.setString(2, matkul);
                ps.setDate(
                    3,
                    java.sql.Date.valueOf(deadline)
                );
                ps.setString(4, status);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                    this,
                    "Tugas berhasil disimpan ke database!"
                );

                namaField.setText("");
                matkulField.setText("");
                statusBox.setSelectedIndex(0);

                muatTugas();
                cardLayout.show(contentPanel, "daftar");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan tugas: "
                    + ex.getMessage()
                );
            }
        });

        tombolBatal.addActionListener(e ->
            cardLayout.show(contentPanel, "daftar")
        );

        panel.add(judul, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        return panel;
    }

    // Memuat daftar tugas dari database
    private void muatTugas() {
        String sql = "SELECT id, nama_tugas, mata_kuliah, "
            + "deadline, status FROM tugas ORDER BY id DESC";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps =
                 conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            taskModel.clear();
            taskIds.clear();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama_tugas");
                String matkul = rs.getString("mata_kuliah");
                String deadline = rs.getString("deadline");
                String status = rs.getString("status");

                taskIds.add(id);

                taskModel.addElement(
                    nama + " | " + matkul
                    + " | Deadline: " + deadline
                    + " | " + status
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal memuat tugas dari database:\n"
                + ex.getMessage()
            );
        }
    }

    // Menghapus tugas dari database
    private void hapusTugas(int id) {
        String sql = "DELETE FROM tugas WHERE id = ?";

        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int hasil = ps.executeUpdate();

            if (hasil > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Tugas berhasil dihapus."
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Data tugas tidak ditemukan."
                );
            }

            muatTugas();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus tugas:\n"
                + ex.getMessage()
            );
        }
    }

    // Mengatur jumlah hari sesuai bulan dan tahun
    private void perbaruiTanggal() {
        if (bulanBox == null || tahunBox == null
                || tanggalBox == null) {
            return;
        }

        if (bulanBox.getSelectedIndex() < 0
                || tahunBox.getSelectedItem() == null) {
            return;
        }

        int tanggalLama = 1;

        if (tanggalBox.getSelectedItem() != null) {
            tanggalLama = Integer.parseInt(
                (String) tanggalBox.getSelectedItem()
            );
        }

        int bulan = bulanBox.getSelectedIndex();
        int tahun = Integer.parseInt(
            (String) tahunBox.getSelectedItem()
        );

        GregorianCalendar kalender =
            new GregorianCalendar(tahun, bulan, 1);

        int jumlahHari = kalender.getActualMaximum(
            Calendar.DAY_OF_MONTH
        );

        tanggalBox.removeAllItems();

        for (int tanggal = 1;
                tanggal <= jumlahHari; tanggal++) {
            tanggalBox.addItem(String.valueOf(tanggal));
        }

        tanggalBox.setSelectedItem(
            String.valueOf(
                Math.min(tanggalLama, jumlahHari)
            )
        );
    }

    // Halaman Tentang
    private JPanel halamanTentang() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel isi = new JLabel(
            "<html><h1>Tentang RuangTugas</h1>"
            + "<p>RuangTugas adalah aplikasi untuk membantu "
            + "mengelola tugas kuliah.</p>"
            + "<p>Fitur: daftar tugas, tambah tugas, "
            + "dan pengelolaan status.</p>"
            + "<p>Dibuat menggunakan Java Swing "
            + "dan MySQL.</p></html>"
        );

        panel.add(isi, BorderLayout.NORTH);
        return panel;
    }

    // Titik awal program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RuangTugas().setVisible(true);
        });
    }
}
