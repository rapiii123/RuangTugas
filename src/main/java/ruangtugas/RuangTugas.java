package ruangtugas;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class RuangTugas extends JFrame {

    private static final Color BACKGROUND = new Color(244, 247, 251);
    private static final Color SURFACE = Color.WHITE;
    private static final Color SIDEBAR = new Color(24, 39, 67);
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color MUTED = new Color(107, 120, 139);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private DefaultListModel<Tugas> taskModel;
    private JList<Tugas> taskList;
    private JPanel daftarContent;
    private CardLayout daftarCardLayout;
    private JLabel totalCount;
    private JLabel progressCount;
    private JLabel completedCount;

    private JTextField namaField;
    private JTextField matkulField;

    private JComboBox<String> tanggalBox;
    private JComboBox<String> bulanBox;
    private JComboBox<String> tahunBox;
    private JComboBox<String> statusBox;

    public RuangTugas() {
        setTitle("RuangTugas - Manajemen Tugas Mahasiswa");
        setSize(1120, 740);
        setMinimumSize(new Dimension(900, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        taskModel = new DefaultListModel<Tugas>();

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(new EmptyBorder(28, 18, 22, 18));

        JLabel logo = new JLabel("  RT   RuangTugas");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setBorder(new EmptyBorder(0, 3, 32, 0));

        JButton btnDashboard = buatTombol("  Dashboard");
        JButton btnDaftar = buatTombol("  Daftar Tugas");
        JButton btnTambah = buatTombol("  +   Tambah Tugas");
        JButton btnTentang = buatTombol("  Tentang");

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuLabel.setForeground(new Color(148, 163, 184));
        menuLabel.setBorder(new EmptyBorder(0, 8, 10, 0));
        sidebar.add(logo);
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(btnDaftar);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(btnTambah);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(btnTentang);
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("<html>Teman kecil untuk<br>deadline kuliahmu.</html>");
        footer.setForeground(new Color(148, 163, 184));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setBorder(new EmptyBorder(15, 8, 0, 0));
        sidebar.add(footer);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

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

        // Muat data dari database
        muatTugas();
    }

    private JButton buatTombol(String teks) {
        JButton tombol = new JButton(teks);
        tombol.setFocusPainted(false);
        tombol.setBorderPainted(false);
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setBackground(SIDEBAR);
        tombol.setForeground(Color.WHITE);
        tombol.setFont(FONT);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        tombol.setPreferredSize(new Dimension(185, 46));
        tombol.setBorder(new EmptyBorder(0, 10, 0, 10));
        tombol.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tombol.setBackground(new Color(42, 61, 94));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                tombol.setBackground(SIDEBAR);
            }
        });
        return tombol;
    }

    private JPanel halamanDashboard() {
        JPanel panel = halamanDasar();
        panel.setLayout(new BorderLayout(0, 24));
        panel.add(judulHalaman("Dashboard", "Ringkasan aktivitas tugas kuliahmu."), BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        JPanel hero = new JPanel(new BorderLayout(20, 10));
        hero.setBackground(new Color(231, 240, 255));
        hero.setBorder(new EmptyBorder(27, 30, 27, 30));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));

        JPanel heroCopy = new JPanel();
        heroCopy.setOpaque(false);
        heroCopy.setLayout(new BoxLayout(heroCopy, BoxLayout.Y_AXIS));
        JLabel sambutan = new JLabel("Hai, selamat datang!");
        sambutan.setFont(new Font("Segoe UI", Font.BOLD, 23));
        sambutan.setForeground(TEXT);
        JLabel deskripsi = new JLabel("Atur tugasmu hari ini, supaya deadline terasa lebih ringan.");
        deskripsi.setFont(FONT);
        deskripsi.setForeground(MUTED);
        deskripsi.setBorder(new EmptyBorder(9, 0, 0, 0));
        heroCopy.add(sambutan);
        heroCopy.add(deskripsi);

        JButton tombol = tombolAksi("Lihat daftar tugas");
        tombol.addActionListener(e -> {
            muatTugas();
            cardLayout.show(contentPanel, "daftar");
        });
        hero.add(heroCopy, BorderLayout.CENTER);
        hero.add(tombol, BorderLayout.EAST);

        isi.add(hero);
        isi.add(Box.createVerticalStrut(23));
        JLabel ringkasan = new JLabel("Ringkasan tugas");
        ringkasan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ringkasan.setForeground(TEXT);
        isi.add(ringkasan);
        isi.add(Box.createVerticalStrut(13));

        JPanel statistik = new JPanel(new GridLayout(1, 3, 16, 0));
        statistik.setOpaque(false);
        totalCount = new JLabel("0");
        progressCount = new JLabel("0");
        completedCount = new JLabel("0");
        statistik.add(kartuStatistik("Total tugas", totalCount, new Color(59, 130, 246)));
        statistik.add(kartuStatistik("Sedang dikerjakan", progressCount, new Color(245, 158, 11)));
        statistik.add(kartuStatistik("Selesai", completedCount, new Color(16, 185, 129)));
        isi.add(statistik);
        isi.add(Box.createVerticalStrut(22));

        JPanel tip = kartu();
        tip.setLayout(new BorderLayout(12, 0));
        tip.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel tipTitle = new JLabel("Tips kecil");
        tipTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tipTitle.setForeground(TEXT);
        JLabel tipCopy = new JLabel("Pecah tugas besar menjadi langkah sederhana dan mulai lebih awal.");
        tipCopy.setFont(FONT);
        tipCopy.setForeground(MUTED);
        tip.add(tipTitle, BorderLayout.NORTH);
        tip.add(tipCopy, BorderLayout.CENTER);
        isi.add(tip);

        panel.add(isi, BorderLayout.CENTER);

        return panel;
    }

    private JPanel halamanDaftar() {
        JPanel panel = halamanDasar();
        panel.setLayout(new BorderLayout(0, 20));
        panel.add(judulHalaman("Daftar Tugas", "Pantau mata kuliah, deadline, dan progresmu."), BorderLayout.NORTH);

        JPanel daftarCard = kartu();
        daftarCard.setLayout(new BorderLayout());
        daftarCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        taskList = new JList<Tugas>(taskModel);
        taskList.setFont(FONT);
        taskList.setBackground(SURFACE);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFixedCellHeight(82);
        taskList.setCellRenderer(new TugasRenderer());

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(SURFACE);

        daftarCardLayout = new CardLayout();
        daftarContent = new JPanel(daftarCardLayout);
        daftarContent.setBackground(SURFACE);
        daftarContent.add(scrollPane, "list");
        JPanel emptyState = new JPanel();
        emptyState.setBackground(SURFACE);
        emptyState.setLayout(new BoxLayout(emptyState, BoxLayout.Y_AXIS));
        JLabel emptyTitle = new JLabel("Belum ada tugas");
        emptyTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyTitle.setForeground(TEXT);
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel emptyCopy = new JLabel("Tugas yang kamu tambahkan akan tampil di sini.");
        emptyCopy.setFont(FONT);
        emptyCopy.setForeground(MUTED);
        emptyCopy.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyState.add(Box.createVerticalGlue());
        emptyState.add(emptyTitle);
        emptyState.add(Box.createVerticalStrut(8));
        emptyState.add(emptyCopy);
        emptyState.add(Box.createVerticalGlue());
        daftarContent.add(emptyState, "empty");
        daftarCard.add(daftarContent, BorderLayout.CENTER);

        JButton tombolHapus = tombolSekunder("Hapus tugas");

        tombolHapus.addActionListener(e -> {
            Tugas terpilih = taskList.getSelectedValue();
            if (terpilih == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pilih tugas yang ingin dihapus."
                );
                return;
            }

            int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus tugas ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
            );

            if (konfirmasi == JOptionPane.YES_OPTION) {
                hapusTugas(terpilih.getId());
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(tombolHapus);
        panel.add(daftarCard, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel halamanTambah() {
        JPanel panel = halamanDasar();
        panel.setLayout(new BorderLayout(0, 20));
        panel.add(judulHalaman("Tambah Tugas", "Catat tugas baru agar tidak ada deadline yang terlewat."), BorderLayout.NORTH);

        JPanel formCard = kartu();
        formCard.setLayout(new BorderLayout(0, 20));
        formCard.setBorder(new EmptyBorder(26, 28, 24, 28));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 18, 14);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        namaField = new JTextField();
        matkulField = new JTextField();
        gayaInput(namaField);
        gayaInput(matkulField);

        // Dropdown tanggal
        tanggalBox = new JComboBox<String>();

        // Dropdown bulan
        bulanBox = new JComboBox<String>(
            new String[] {
                "Januari", "Februari", "Maret",
                "April", "Mei", "Juni",
                "Juli", "Agustus", "September",
                "Oktober", "November", "Desember"
            }
        );

        // Dropdown tahun
        tahunBox = new JComboBox<String>();

        int tahunSekarang = LocalDate.now().getYear();
        for (int tahun = tahunSekarang; tahun <= tahunSekarang + 10; tahun++) {
            tahunBox.addItem(String.valueOf(tahun));
        }

        LocalDate hariIni = LocalDate.now();
        bulanBox.setSelectedIndex(hariIni.getMonthValue() - 1);
        tahunBox.setSelectedItem(String.valueOf(tahunSekarang));

        JPanel panelDeadline = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        panelDeadline.setOpaque(false);

        panelDeadline.add(tanggalBox);
        panelDeadline.add(bulanBox);
        panelDeadline.add(tahunBox);

        bulanBox.addActionListener(e -> perbaruiTanggal());
        tahunBox.addActionListener(e -> perbaruiTanggal());

        perbaruiTanggal();
        tanggalBox.setSelectedItem(String.valueOf(hariIni.getDayOfMonth()));

        // Dropdown status
        statusBox = new JComboBox<String>(
            new String[] {
                "Belum Dikerjakan",
                "Sedang Dikerjakan",
                "Selesai"
            }
        );

        gayaCombo(tanggalBox);
        gayaCombo(bulanBox);
        gayaCombo(tahunBox);
        gayaCombo(statusBox);

        tambahBarisForm(form, gbc, 0, "Nama tugas", namaField);
        tambahBarisForm(form, gbc, 1, "Mata kuliah", matkulField);
        tambahBarisForm(form, gbc, 2, "Deadline", panelDeadline);
        tambahBarisForm(form, gbc, 3, "Status", statusBox);

        JButton tombolSimpan = tombolAksi("Simpan tugas");
        JButton tombolBatal = tombolSekunder("Batal");
        JPanel formActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        formActions.setOpaque(false);
        formActions.add(tombolBatal);
        formActions.add(tombolSimpan);

        // Simpan tugas menggunakan Hibernate
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

            LocalDate deadline;

            try {
                deadline = LocalDate.of(
                    tahun, bulan, tanggal
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Tanggal deadline tidak valid."
                );
                return;
            }

            String status =
                (String) statusBox.getSelectedItem();

            Tugas tugas = new Tugas(
                nama, matkul, deadline, status
            );

            Transaction transaction = null;

            try (Session session =
                    HibernateUtil.getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                session.persist(tugas);

                transaction.commit();

                JOptionPane.showMessageDialog(
                    this,
                    "Tugas berhasil disimpan ke database!"
                );

                namaField.setText("");
                matkulField.setText("");
                statusBox.setSelectedIndex(0);

                muatTugas();

                cardLayout.show(contentPanel, "daftar");

            } catch (Exception ex) {

                if (transaction != null
                        && transaction.isActive()) {
                    transaction.rollback();
                }

                JOptionPane.showMessageDialog(
                    this,
                    "Gagal menyimpan tugas:\n"
                    + ex.getMessage()
                );
            }
        });

        tombolBatal.addActionListener(e ->
            cardLayout.show(contentPanel, "daftar")
        );

        formCard.add(form, BorderLayout.NORTH);
        formCard.add(formActions, BorderLayout.SOUTH);
        panel.add(formCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel halamanDasar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(34, 38, 32, 38));
        return panel;
    }

    private JPanel judulHalaman(String judul, String deskripsi) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(judul);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT);
        JLabel descriptionLabel = new JLabel(deskripsi);
        descriptionLabel.setFont(FONT);
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        header.add(titleLabel);
        header.add(descriptionLabel);
        return header;
    }

    private JPanel kartu() {
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setBorder(new LineBorder(BORDER, 1, true));
        return card;
    }

    private JPanel kartuStatistik(String label, JLabel nilai, Color accent) {
        JPanel card = kartu();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(19, 20, 19, 20));
        JLabel labelStat = new JLabel(label);
        labelStat.setFont(FONT);
        labelStat.setForeground(MUTED);
        nilai.setFont(new Font("Segoe UI", Font.BOLD, 30));
        nilai.setForeground(accent);
        card.add(labelStat, BorderLayout.NORTH);
        card.add(nilai, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(180, 110));
        return card;
    }

    private JButton tombolAksi(String teks) {
        JButton tombol = new JButton(teks);
        tombol.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tombol.setForeground(Color.WHITE);
        tombol.setBackground(ACCENT);
        tombol.setFocusPainted(false);
        tombol.setBorderPainted(false);
        tombol.setBorder(new EmptyBorder(12, 18, 12, 18));
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tombol;
    }

    private JButton tombolSekunder(String teks) {
        JButton tombol = new JButton(teks);
        tombol.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tombol.setForeground(TEXT);
        tombol.setBackground(SURFACE);
        tombol.setFocusPainted(false);
        tombol.setBorder(new LineBorder(BORDER, 1, true));
        tombol.setPreferredSize(new Dimension(135, 42));
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tombol;
    }

    private void gayaInput(JTextField input) {
        input.setFont(FONT);
        input.setForeground(TEXT);
        input.setBackground(SURFACE);
        input.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        input.setPreferredSize(new Dimension(300, 43));
    }

    private void gayaCombo(JComboBox<String> combo) {
        combo.setFont(FONT);
        combo.setForeground(TEXT);
        combo.setBackground(SURFACE);
        combo.setBorder(new LineBorder(BORDER, 1, true));
        combo.setPreferredSize(new Dimension(145, 42));
    }

    private void tambahBarisForm(JPanel form, GridBagConstraints gbc,
            int baris, String label, JComponent input) {
        gbc.gridy = baris;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fieldLabel.setForeground(TEXT);
        fieldLabel.setPreferredSize(new Dimension(115, 40));
        form.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        if (!(input instanceof JPanel)) {
            input.setPreferredSize(new Dimension(300, 42));
        }
        form.add(input, gbc);
        gbc.insets = new Insets(0, 0, 18, 14);
    }

    private void perbaruiRingkasan() {
        if (totalCount == null) {
            return;
        }
        int sedangDikerjakan = 0;
        int selesai = 0;
        for (int i = 0; i < taskModel.size(); i++) {
            String status = taskModel.get(i).getStatus();
            if ("Sedang Dikerjakan".equalsIgnoreCase(status)) {
                sedangDikerjakan++;
            } else if ("Selesai".equalsIgnoreCase(status)) {
                selesai++;
            }
        }
        totalCount.setText(String.valueOf(taskModel.size()));
        progressCount.setText(String.valueOf(sedangDikerjakan));
        completedCount.setText(String.valueOf(selesai));
        if (daftarCardLayout != null) {
            daftarCardLayout.show(daftarContent, taskModel.isEmpty() ? "empty" : "list");
        }
    }

    private class TugasRenderer extends JPanel implements ListCellRenderer<Tugas> {
        private final JLabel nama = new JLabel();
        private final JLabel detail = new JLabel();
        private final JLabel status = new JLabel();

        TugasRenderer() {
            setLayout(new BorderLayout(12, 5));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(12, 16, 12, 16)
            ));
            JPanel copy = new JPanel();
            copy.setOpaque(false);
            copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
            nama.setFont(new Font("Segoe UI", Font.BOLD, 15));
            detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detail.setForeground(MUTED);
            copy.add(nama);
            copy.add(Box.createVerticalStrut(5));
            copy.add(detail);
            status.setFont(new Font("Segoe UI", Font.BOLD, 12));
            add(copy, BorderLayout.CENTER);
            add(status, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Tugas> list,
                Tugas tugas, int index, boolean isSelected, boolean cellHasFocus) {
            nama.setText(tugas.getNamaTugas());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("id", "ID"));
            detail.setText(tugas.getMataKuliah() + "  \u2022  Deadline "
                + tugas.getDeadline().format(formatter));
            status.setText(tugas.getStatus());
            status.setForeground("Selesai".equalsIgnoreCase(tugas.getStatus())
                ? new Color(5, 150, 105)
                : "Sedang Dikerjakan".equalsIgnoreCase(tugas.getStatus())
                    ? new Color(217, 119, 6) : MUTED);
            Color background = isSelected ? new Color(239, 246, 255) : SURFACE;
            setBackground(background);
            nama.setForeground(TEXT);
            return this;
        }
    }

    private void muatTugas() {

        try (Session session =
                HibernateUtil.getSessionFactory().openSession()) {

            taskModel.clear();

            java.util.List<Tugas> daftarTugas =
                session.createQuery(
                    "FROM Tugas t ORDER BY t.id DESC",
                    Tugas.class
                ).getResultList();

            for (Tugas tugas : daftarTugas) {
                taskModel.addElement(tugas);
            }
            perbaruiRingkasan();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal memuat tugas dari database:\n"
                + ex.getMessage()
            );
        }
    }

    // Menghapus tugas menggunakan Hibernate
    private void hapusTugas(int id) {

        Transaction transaction = null;

        try (Session session =
                HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            Tugas tugas = session.get(Tugas.class, id);

            if (tugas != null) {

                session.remove(tugas);

                transaction.commit();

                JOptionPane.showMessageDialog(
                    this,
                    "Tugas berhasil dihapus."
                );

            } else {

                transaction.commit();

                JOptionPane.showMessageDialog(
                    this,
                    "Data tugas tidak ditemukan."
                );
            }

            muatTugas();

        } catch (Exception ex) {

            if (transaction != null
                    && transaction.isActive()) {
                transaction.rollback();
            }

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

    private JPanel halamanTentang() {
        JPanel panel = halamanDasar();
        panel.setLayout(new BorderLayout(0, 22));
        panel.add(judulHalaman("Tentang RuangTugas", "Kenali aplikasi yang membantu tugas kuliah tetap teratur."), BorderLayout.NORTH);

        JPanel about = kartu();
        about.setLayout(new BoxLayout(about, BoxLayout.Y_AXIS));
        about.setBorder(new EmptyBorder(30, 32, 30, 32));
        JLabel badge = new JLabel("RUANG BELAJAR, TUGAS LEBIH TERATUR");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(ACCENT);
        JLabel headline = new JLabel("Fokus pada belajar. Deadline tetap terpantau.");
        headline.setFont(new Font("Segoe UI", Font.BOLD, 23));
        headline.setForeground(TEXT);
        headline.setBorder(new EmptyBorder(12, 0, 10, 0));
        JLabel description = new JLabel("<html><div style='width: 560px'>"
            + "RuangTugas adalah aplikasi desktop sederhana untuk membantu mahasiswa "
            + "mencatat tugas, mengatur deadline, dan melihat progres pengerjaan "
            + "dalam satu tempat.</div></html>");
        description.setFont(FONT);
        description.setForeground(MUTED);
        about.add(badge);
        about.add(headline);
        about.add(description);
        about.add(Box.createVerticalStrut(26));

        JPanel features = new JPanel(new GridLayout(1, 3, 12, 0));
        features.setOpaque(false);
        features.add(kartuFitur("01", "Catat tugas", "Simpan nama tugas dan mata kuliah."));
        features.add(kartuFitur("02", "Atur deadline", "Pilih tanggal yang sesuai dengan jadwalmu."));
        features.add(kartuFitur("03", "Pantau progres", "Lihat status tugas dari satu daftar."));
        about.add(features);
        about.add(Box.createVerticalStrut(26));
        JLabel tech = new JLabel("Dibangun dengan Java Swing, Hibernate, dan MySQL.");
        tech.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tech.setForeground(MUTED);
        about.add(tech);
        panel.add(about, BorderLayout.CENTER);
        return panel;
    }

    private JPanel kartuFitur(String nomor, String judul, String keterangan) {
        JPanel feature = kartu();
        feature.setLayout(new BoxLayout(feature, BoxLayout.Y_AXIS));
        feature.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel number = new JLabel(nomor);
        number.setFont(new Font("Segoe UI", Font.BOLD, 12));
        number.setForeground(ACCENT);
        JLabel title = new JLabel(judul);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(8, 0, 5, 0));
        JLabel copy = new JLabel("<html><div style='width: 115px'>" + keterangan + "</div></html>");
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copy.setForeground(MUTED);
        feature.add(number);
        feature.add(title);
        feature.add(copy);
        return feature;
    }

    // Titik awal program
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                new RuangTugas().setVisible(true);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    null,
                    "Aplikasi gagal dijalankan:\n"
                    + ex.getMessage()
                );

                ex.printStackTrace();

                HibernateUtil.shutdown();
            }
        });
    }
}