package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Form Perbaikan / Koreksi Data Rekam Medis
 * Regulasi: PMK No. 24 Tahun 2022
 * RS Rafflesia Bengkulu
 */
public class RMKoreksiRM extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();

    private String nikPengaju = "";
    private String namaPengaju = "";
    private String jbtnPengaju = "";

    // Komponen UI
    private JTabbedPane tabPane;
    private JTextField tfNoRM, tfNamaPasien, tfNoReg, tfDasarRegulasi;
    private JComboBox<String> cbJenisLayanan, cbBagianData;
    private com.toedter.calendar.JDateChooser dcTglDataAsli;
    private JTextArea taDataLama, taDataBaru, taAlasan;
    private JButton btnSubmit, btnBatal, btnRefreshDaftar;
    private JTable tblDaftar;
    private DefaultTableModel modelDaftar;

    public RMKoreksiRM(java.awt.Frame parent, boolean modal,
        String noRawat, String noRM, String namaPasien) {
    super(parent, modal);
    ambilDataPengaju();
    initComponents();
    loadDaftarPengajuan();
    // Isi otomatis dari data pasien yang dipilih
    tfNoRM.setText(noRM);
    tfNamaPasien.setText(namaPasien);
    tfNoReg.setText(noRawat);
    setTitle("Perbaikan Data Rekam Medis - " + namaPengaju);
    setSize(870, 640);
    setLocationRelativeTo(parent);
    }

    private void ambilDataPengaju() {
        nikPengaju = akses.getkode();
        if (nikPengaju.equals("Admin Utama")) {
            namaPengaju = "Admin Utama";
            jbtnPengaju = "Administrator";
        } else {
            try {
                PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT nama, jbtn FROM pegawai WHERE nik = ?");
                ps.setString(1, nikPengaju);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    namaPengaju = rs.getString("nama") != null ? rs.getString("nama") : nikPengaju;
                    jbtnPengaju = rs.getString("jbtn") != null ? rs.getString("jbtn") : "-";
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                namaPengaju = nikPengaju;
                jbtnPengaju = "-";
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        tabPane = new JTabbedPane();
        tabPane.setFont(new Font("Tahoma", Font.PLAIN, 11));
        tabPane.addTab("  Pengajuan Koreksi  ", buatTabForm());
        tabPane.addTab("  Daftar Pengajuan Saya  ", buatTabDaftar());
        add(tabPane);
    }

    private JPanel buatTabForm() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(new Color(34, 120, 34));
        pnlHeader.setPreferredSize(new Dimension(0, 52));
        JLabel lblJudul = new JLabel("   FORMULIR PERBAIKAN DATA REKAM MEDIS");
        lblJudul.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("   Dasar: PMK No. 24 Tahun 2022  |  Pengaju: "
            + namaPengaju + "  (" + jbtnPengaju + ")");
        lblSub.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblSub.setForeground(new Color(210, 255, 210));
        pnlHeader.add(lblJudul);
        pnlHeader.add(lblSub);
        panel.add(pnlHeader, BorderLayout.NORTH);

        // Form
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(34, 120, 34)),
            "Data Pasien & Koreksi"));
        pnlForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Baris 1: No RM + Nama Pasien
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(buatLabel("No. RM *"), gbc);
        gbc.gridx = 1; tfNoRM = new JTextField(15); pnlForm.add(tfNoRM, gbc);
        gbc.gridx = 2; pnlForm.add(buatLabel("Nama Pasien *"), gbc);
        gbc.gridx = 3; tfNamaPasien = new JTextField(22); pnlForm.add(tfNamaPasien, gbc);

        // Baris 2: No Registrasi + Tgl Data Asli
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(buatLabel("No. Registrasi"), gbc);
        gbc.gridx = 1; tfNoReg = new JTextField(15); pnlForm.add(tfNoReg, gbc);
        gbc.gridx = 2; pnlForm.add(buatLabel("Tgl. Data Asli *"), gbc);
        gbc.gridx = 3;
        dcTglDataAsli = new com.toedter.calendar.JDateChooser();
        dcTglDataAsli.setPreferredSize(new Dimension(160, 25));
        dcTglDataAsli.setDateFormatString("dd-MM-yyyy");
        pnlForm.add(dcTglDataAsli, gbc);

        // Baris 3: Jenis Layanan + Bagian Data
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(buatLabel("Jenis Layanan *"), gbc);
        gbc.gridx = 1;
        cbJenisLayanan = new JComboBox<>(new String[]{
            "Rawat Jalan", "Rawat Inap", "IGD"});
        pnlForm.add(cbJenisLayanan, gbc);
        gbc.gridx = 2; pnlForm.add(buatLabel("Bagian Data *"), gbc);
        gbc.gridx = 3;
        cbBagianData = new JComboBox<>(new String[]{
            "CPPT / SOAP", "Diagnosa (ICD-10)", "Tindakan / Prosedur",
            "Hasil Pemeriksaan Fisik", "Resume Medis", "Catatan Keperawatan",
            "Catatan Gizi", "Catatan Farmasi", "Asesmen Awal",
            "Triase IGD", "Catatan Anestesi / Sedasi", "Lain-lain"});
        pnlForm.add(cbBagianData, gbc);

        // Baris 4: Dasar Regulasi
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(buatLabel("Dasar Regulasi"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        tfDasarRegulasi = new JTextField("PMK No. 24 Tahun 2022 tentang Rekam Medis");
        tfDasarRegulasi.setPreferredSize(new Dimension(460, 25));
        pnlForm.add(tfDasarRegulasi, gbc);
        gbc.gridwidth = 1;

        // Baris 5: Data Lama
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST;
        pnlForm.add(buatLabel("Data Lama *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        taDataLama = buatTextArea(new Color(255, 240, 240));
        JScrollPane spLama = new JScrollPane(taDataLama);
        spLama.setPreferredSize(new Dimension(460, 85));
        pnlForm.add(spLama, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;

        // Baris 6: Data Baru
        gbc.gridx = 0; gbc.gridy = 5;
        pnlForm.add(buatLabel("Data Baru *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        taDataBaru = buatTextArea(new Color(240, 255, 240));
        JScrollPane spBaru = new JScrollPane(taDataBaru);
        spBaru.setPreferredSize(new Dimension(460, 85));
        pnlForm.add(spBaru, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;

        // Baris 7: Alasan
        gbc.gridx = 0; gbc.gridy = 6;
        pnlForm.add(buatLabel("Alasan Koreksi *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        taAlasan = buatTextArea(new Color(240, 240, 255));
        JScrollPane spAlasan = new JScrollPane(taAlasan);
        spAlasan.setPreferredSize(new Dimension(460, 70));
        pnlForm.add(spAlasan, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;

        panel.add(new JScrollPane(pnlForm), BorderLayout.CENTER);

        // Tombol
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlBtn.setBackground(new Color(245, 245, 245));
        btnBatal = new JButton("Bersihkan Form");
        btnBatal.setFont(new Font("Tahoma", Font.PLAIN, 11));
        btnBatal.addActionListener(e -> bersihkanForm());
        btnSubmit = new JButton("  Ajukan Koreksi  ");
        btnSubmit.setBackground(new Color(34, 120, 34));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSubmit.addActionListener(e -> submitKoreksi());
        pnlBtn.add(btnBatal);
        pnlBtn.add(btnSubmit);
        panel.add(pnlBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buatTabDaftar() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        String[] kolom = {"ID", "No. RM", "Nama Pasien", "Bagian Data", "Tgl Pengajuan", "Status"};
        modelDaftar = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDaftar = new JTable(modelDaftar);
        tblDaftar.setRowHeight(22);
        tblDaftar.setFont(new Font("Tahoma", Font.PLAIN, 11));
        tblDaftar.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        tblDaftar.getColumnModel().getColumn(0).setMaxWidth(45);
        tblDaftar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDaftar.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String st = t.getValueAt(r, 5) != null ? t.getValueAt(r, 5).toString() : "";
                    if ("Disetujui".equals(st)) setBackground(new Color(220, 255, 220));
                    else if ("Ditolak".equals(st)) setBackground(new Color(255, 220, 220));
                    else setBackground(new Color(255, 255, 220));
                }
                return this;
            }
        });
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnRefreshDaftar = new JButton("Refresh");
        btnRefreshDaftar.addActionListener(e -> loadDaftarPengajuan());
        JButton btnDetail = new JButton("Lihat Detail");
        btnDetail.addActionListener(e -> lihatDetailPengajuan());
        pnlTop.add(btnRefreshDaftar);
        pnlTop.add(btnDetail);
        panel.add(pnlTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblDaftar), BorderLayout.CENTER);
        JLabel lblKet = new JLabel("  Kuning = Menunggu  |  Hijau = Disetujui  |  Merah = Ditolak");
        lblKet.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblKet.setForeground(Color.GRAY);
        panel.add(lblKet, BorderLayout.SOUTH);
        return panel;
    }

    private void submitKoreksi() {
        if (tfNoRM.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No. RM wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            tfNoRM.requestFocus(); return;
        }
        if (tfNamaPasien.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Pasien wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            tfNamaPasien.requestFocus(); return;
        }
        if (dcTglDataAsli.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal Data Asli wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (taDataLama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data Lama wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            taDataLama.requestFocus(); return;
        }
        if (taDataBaru.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data Baru (Koreksi) wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            taDataBaru.requestFocus(); return;
        }
        if (taAlasan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alasan Koreksi wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            taAlasan.requestFocus(); return;
        }
        // Validasi 2x24 jam
        java.util.Date tglAsli = dcTglDataAsli.getDate();
        long selisihJam = (new Date().getTime() - tglAsli.getTime()) / (1000 * 60 * 60);
        if (selisihJam < 48) {
            JOptionPane.showMessageDialog(this,
                "Data belum melewati 2x24 jam dari tanggal data asli.\n"
                + "Formulir ini hanya untuk koreksi data lebih dari 48 jam.",
                "Tidak Memenuhi Syarat", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Ajukan perbaikan data rekam medis ini?\n"
            + "Pengajuan akan masuk ke antrian persetujuan Kabid & Direktur.",
            "Konfirmasi Pengajuan", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO koreksi_rm (no_rm, nama_pasien, tgl_data_asli, no_registrasi, "
                + "jenis_layanan, bagian_data, isi_data_lama, isi_data_baru, alasan_koreksi, "
                + "dasar_regulasi, pengaju_nik, pengaju_nama, tgl_pengajuan, status) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'Menunggu Persetujuan')");
            ps.setString(1, tfNoRM.getText().trim());
            ps.setString(2, tfNamaPasien.getText().trim());
            ps.setString(3, sdf.format(tglAsli));
            ps.setString(4, tfNoReg.getText().trim());
            ps.setString(5, cbJenisLayanan.getSelectedItem().toString());
            ps.setString(6, cbBagianData.getSelectedItem().toString());
            ps.setString(7, taDataLama.getText().trim());
            ps.setString(8, taDataBaru.getText().trim());
            ps.setString(9, taAlasan.getText().trim());
            ps.setString(10, tfDasarRegulasi.getText().trim());
            ps.setString(11, nikPengaju);
            ps.setString(12, namaPengaju);
            ps.executeUpdate();
            ps.close();

            // Ambil ID baru
            PreparedStatement psId = koneksi.prepareStatement("SELECT LAST_INSERT_ID() as id");
            ResultSet rsId = psId.executeQuery();
            int koreksiId = 0;
            if (rsId.next()) koreksiId = rsId.getInt("id");
            rsId.close(); psId.close();

            // Buat approval chain
            if (koreksiId > 0) buatApprovalChain(koreksiId);

            JOptionPane.showMessageDialog(this,
                "Pengajuan koreksi berhasil diajukan!\n"
                + "ID Pengajuan: #" + koreksiId + "\n"
                + "Silahkan menunggu persetujuan dari Kabid & Direktur.",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            bersihkanForm();
            loadDaftarPengajuan();
            tabPane.setSelectedIndex(1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan pengajuan:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buatApprovalChain(int koreksiId) throws Exception {
        String[][] chain = {
            {"1", "Kabid Keperawatan"},
            {"2", "Kabid Yanmed"},
            {"3", "Kabag Umum & SDM"},
            {"4", "Direktur"}
        };
        for (String[] level : chain) {
            PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO koreksi_rm_approval (koreksi_id, urutan, jabatan_required, status) "
                + "VALUES (?,?,?,'Menunggu')");
            ps.setInt(1, koreksiId);
            ps.setInt(2, Integer.parseInt(level[0]));
            ps.setString(3, level[1]);
            ps.executeUpdate();
            ps.close();
        }
    }

    private void loadDaftarPengajuan() {
        modelDaftar.setRowCount(0);
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                "SELECT id, no_rm, nama_pasien, bagian_data, "
                + "DATE_FORMAT(tgl_pengajuan,'%d-%m-%Y %H:%i') as tgl, status "
                + "FROM koreksi_rm WHERE pengaju_nik = ? ORDER BY tgl_pengajuan DESC");
            ps.setString(1, nikPengaju);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelDaftar.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("no_rm"), rs.getString("nama_pasien"),
                    rs.getString("bagian_data"), rs.getString("tgl"), rs.getString("status")
                });
            }
            rs.close(); ps.close();
        } catch (Exception e) { /* fail-safe */ }
    }

    private void lihatDetailPengajuan() {
        int row = tblDaftar.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pengajuan terlebih dahulu!");
            return;
        }
        int id = (int) modelDaftar.getValueAt(row, 0);
        new RMDetailKoreksiRM(null, true, id).setVisible(true);
    }

    private void bersihkanForm() {
        tfNoRM.setText(""); tfNamaPasien.setText(""); tfNoReg.setText("");
        dcTglDataAsli.setDate(null);
        taDataLama.setText(""); taDataBaru.setText(""); taAlasan.setText("");
        cbJenisLayanan.setSelectedIndex(0); cbBagianData.setSelectedIndex(0);
        tfDasarRegulasi.setText("PMK No. 24 Tahun 2022 tentang Rekam Medis");
        tfNoRM.requestFocus();
    }

    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
        return lbl;
    }

    private JTextArea buatTextArea(Color bg) {
        JTextArea ta = new JTextArea();
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setFont(new Font("Tahoma", Font.PLAIN, 11));
        ta.setBackground(bg);
        ta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(3, 4, 3, 4)));
        return ta;
    }
}