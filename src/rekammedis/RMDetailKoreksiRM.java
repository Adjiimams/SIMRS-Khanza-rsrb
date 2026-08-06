package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Dialog Detail Koreksi Rekam Medis
 * Menampilkan data lengkap + riwayat approval chain
 * RS Rafflesia Bengkulu
 */
public class RMDetailKoreksiRM extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private int koreksiId;

    private JTextArea taDataLama, taDataBaru, taAlasan;
    private JTable tblApproval;
    private DefaultTableModel modelApproval;
    private JLabel lblStatus, lblNoRM, lblNama, lblBagian, lblJenisLayanan,
                   lblTglAsli, lblTglAjuan, lblPengaju, lblRegulasi, lblNoReg;

    public RMDetailKoreksiRM(java.awt.Frame parent, boolean modal, int koreksiId) {
        super(parent, modal);
        this.koreksiId = koreksiId;
        initComponents();
        loadData();
        setTitle("Detail Koreksi Rekam Medis  #" + koreksiId);
        setSize(760, 620);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(4, 4));

        // Header
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlHeader.setBackground(new Color(45, 45, 75));
        pnlHeader.setPreferredSize(new Dimension(0, 48));
        JLabel lblJudul = new JLabel("DETAIL PENGAJUAN KOREKSI  #" + koreksiId);
        lblJudul.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblJudul.setForeground(Color.WHITE);
        lblStatus = new JLabel("[ Memuat... ]");
        lblStatus.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblStatus.setForeground(new Color(255, 230, 80));
        pnlHeader.add(lblJudul);
        pnlHeader.add(lblStatus);
        add(pnlHeader, BorderLayout.NORTH);

        // Split atas-bawah
        JSplitPane splitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitMain.setDividerLocation(310);
        splitMain.setResizeWeight(0.55);
        splitMain.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        // Panel atas
        JPanel pnlAtas = new JPanel(new BorderLayout(5, 5));

        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(45, 45, 75)),
            "Informasi Pasien & Pengajuan"));
        pnlInfo.setBackground(new Color(250, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; pnlInfo.add(buatLabelKey("No. RM :"), gbc);
        gbc.gridx = 1; lblNoRM = buatLabelVal("-"); pnlInfo.add(lblNoRM, gbc);
        gbc.gridx = 2; pnlInfo.add(buatLabelKey("Nama Pasien :"), gbc);
        gbc.gridx = 3; lblNama = buatLabelVal("-"); pnlInfo.add(lblNama, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlInfo.add(buatLabelKey("No. Registrasi :"), gbc);
        gbc.gridx = 1; lblNoReg = buatLabelVal("-"); pnlInfo.add(lblNoReg, gbc);
        gbc.gridx = 2; pnlInfo.add(buatLabelKey("Jenis Layanan :"), gbc);
        gbc.gridx = 3; lblJenisLayanan = buatLabelVal("-"); pnlInfo.add(lblJenisLayanan, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlInfo.add(buatLabelKey("Bagian Data :"), gbc);
        gbc.gridx = 1; lblBagian = buatLabelVal("-"); pnlInfo.add(lblBagian, gbc);
        gbc.gridx = 2; pnlInfo.add(buatLabelKey("Tgl. Data Asli :"), gbc);
        gbc.gridx = 3; lblTglAsli = buatLabelVal("-"); pnlInfo.add(lblTglAsli, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnlInfo.add(buatLabelKey("Tgl. Pengajuan :"), gbc);
        gbc.gridx = 1; lblTglAjuan = buatLabelVal("-"); pnlInfo.add(lblTglAjuan, gbc);
        gbc.gridx = 2; pnlInfo.add(buatLabelKey("Pengaju :"), gbc);
        gbc.gridx = 3; lblPengaju = buatLabelVal("-"); pnlInfo.add(lblPengaju, gbc);

        gbc.gridx = 0; gbc.gridy = 4; pnlInfo.add(buatLabelKey("Dasar Regulasi :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        lblRegulasi = buatLabelVal("-"); pnlInfo.add(lblRegulasi, gbc);
        gbc.gridwidth = 1;

        pnlAtas.add(pnlInfo, BorderLayout.NORTH);

        JTabbedPane tabData = new JTabbedPane();
        tabData.setFont(new Font("Tahoma", Font.PLAIN, 11));
        taDataLama = buatTextAreaRO(new Color(255, 242, 242));
        taDataBaru = buatTextAreaRO(new Color(242, 255, 242));
        taAlasan   = buatTextAreaRO(new Color(242, 242, 255));
        tabData.addTab("  Data Lama  ", new JScrollPane(taDataLama));
        tabData.addTab("  Data Baru (Koreksi)  ", new JScrollPane(taDataBaru));
        tabData.addTab("  Alasan & Keterangan  ", new JScrollPane(taAlasan));
        pnlAtas.add(tabData, BorderLayout.CENTER);
        splitMain.setTopComponent(pnlAtas);

        // Panel bawah - approval chain
        JPanel pnlBawah = new JPanel(new BorderLayout(4, 4));
        JLabel lblTitleA = new JLabel("Riwayat Persetujuan  (Approval Chain)");
        lblTitleA.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblTitleA.setBorder(BorderFactory.createEmptyBorder(6, 2, 4, 0));

        String[] kolom = {"#", "Jabatan", "Approver", "Status", "Tgl Aksi", "Catatan"};
        modelApproval = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblApproval = new JTable(modelApproval);
        tblApproval.setRowHeight(22);
        tblApproval.setFont(new Font("Tahoma", Font.PLAIN, 11));
        tblApproval.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        tblApproval.getColumnModel().getColumn(0).setMaxWidth(35);
        tblApproval.getColumnModel().getColumn(3).setMaxWidth(100);
        tblApproval.getColumnModel().getColumn(4).setMaxWidth(135);
        tblApproval.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String st = t.getValueAt(r, 3) != null ? t.getValueAt(r, 3).toString() : "";
                    if ("Disetujui".equals(st)) setBackground(new Color(220, 255, 220));
                    else if ("Ditolak".equals(st)) setBackground(new Color(255, 220, 220));
                    else setBackground(new Color(255, 255, 215));
                }
                return this;
            }
        });
        pnlBawah.add(lblTitleA, BorderLayout.NORTH);
        pnlBawah.add(new JScrollPane(tblApproval), BorderLayout.CENTER);
        splitMain.setBottomComponent(pnlBawah);
        add(splitMain, BorderLayout.CENTER);

        // Tombol tutup
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBtn.setBackground(new Color(245, 245, 245));
        JButton btnTutup = new JButton("  Tutup  ");
        btnTutup.setFont(new Font("Tahoma", Font.PLAIN, 11));
        btnTutup.addActionListener(e -> dispose());
        pnlBtn.add(btnTutup);
        add(pnlBtn, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                "SELECT * FROM koreksi_rm WHERE id = ?");
            ps.setInt(1, koreksiId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblNoRM.setText(nvl(rs.getString("no_rm")));
                lblNama.setText(nvl(rs.getString("nama_pasien")));
                lblNoReg.setText(nvl(rs.getString("no_registrasi")));
                lblBagian.setText(nvl(rs.getString("bagian_data")));
                lblJenisLayanan.setText(nvl(rs.getString("jenis_layanan")));
                lblTglAsli.setText(nvl(rs.getString("tgl_data_asli")));
                lblTglAjuan.setText(nvl(rs.getString("tgl_pengajuan")));
                lblPengaju.setText(nvl(rs.getString("pengaju_nama"))
                    + "  (" + nvl(rs.getString("pengaju_nik")) + ")");
                lblRegulasi.setText(nvl(rs.getString("dasar_regulasi")));
                taDataLama.setText(nvl(rs.getString("isi_data_lama")));
                taDataBaru.setText(nvl(rs.getString("isi_data_baru")));
                taAlasan.setText(nvl(rs.getString("alasan_koreksi")));
                String ket = rs.getString("keterangan_penolakan");
                if (ket != null && !ket.trim().isEmpty()) {
                    taAlasan.setText(taAlasan.getText()
                        + "\n\n─────────────────────\nKETERANGAN PENOLAKAN:\n" + ket);
                }
                String status = nvl(rs.getString("status"));
                lblStatus.setText("[ " + status + " ]");
                if ("Disetujui".equals(status)) lblStatus.setForeground(new Color(80, 220, 80));
                else if ("Ditolak".equals(status)) lblStatus.setForeground(new Color(255, 80, 80));
                else lblStatus.setForeground(new Color(255, 220, 60));
            }
            rs.close(); ps.close();

            // Approval chain
            modelApproval.setRowCount(0);
            PreparedStatement ps2 = koneksi.prepareStatement(
                "SELECT urutan, jabatan_required, approver_nama, status, "
                + "DATE_FORMAT(tgl_aksi,'%d-%m-%Y %H:%i') as tgl_aksi, catatan "
                + "FROM koreksi_rm_approval WHERE koreksi_id = ? ORDER BY urutan ASC");
            ps2.setInt(1, koreksiId);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                modelApproval.addRow(new Object[]{
                    rs2.getInt("urutan"),
                    nvl(rs2.getString("jabatan_required")),
                    nvl(rs2.getString("approver_nama"), "-"),
                    nvl(rs2.getString("status")),
                    nvl(rs2.getString("tgl_aksi"), "-"),
                    nvl(rs2.getString("catatan"), "-")
                });
            }
            rs2.close(); ps2.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nvl(String val) { return val != null ? val : ""; }
    private String nvl(String val, String def) {
        return (val != null && !val.trim().isEmpty()) ? val : def;
    }

    private JLabel buatLabelKey(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
        lbl.setForeground(new Color(60, 60, 100));
        return lbl;
    }

    private JLabel buatLabelVal(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        return lbl;
    }

    private JTextArea buatTextAreaRO(Color bg) {
        JTextArea ta = new JTextArea();
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setFont(new Font("Tahoma", Font.PLAIN, 11));
        ta.setBackground(bg);
        ta.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return ta;
    }
}