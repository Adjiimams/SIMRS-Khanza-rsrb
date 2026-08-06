package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Form Approval Koreksi Rekam Medis
 * Untuk: Kabid Keperawatan, Kabid Yanmed, Kabag Umum & SDM, Direktur
 * RS Rafflesia Bengkulu
 */
public class RMApprovalKoreksiRM extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();

    private String nikApprover = "";
    private String namaApprover = "";
    private String jbtnApprover = "";
    private int urutanApprover = 0;

    private JTable tblPengajuan;
    private DefaultTableModel modelPengajuan;
    private JButton btnRefresh, btnSetujui, btnTolak, btnDetail;

    public RMApprovalKoreksiRM(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        ambilDataApprover();
        initComponents();
        if (urutanApprover > 0) loadPengajuan();
        setTitle("Approval Koreksi Rekam Medis - " + namaApprover);
        setSize(920, 560);
        setLocationRelativeTo(parent);
    }

    private void ambilDataApprover() {
        nikApprover = akses.getkode();
        if (nikApprover.equals("Admin Utama")) {
            namaApprover = "Admin Utama";
            jbtnApprover = "Administrator";
        } else {
            try {
                PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT nama, jbtn FROM pegawai WHERE nik = ?");
                ps.setString(1, nikApprover);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    namaApprover = rs.getString("nama") != null ? rs.getString("nama") : nikApprover;
                    jbtnApprover = rs.getString("jbtn") != null ? rs.getString("jbtn") : "";
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                namaApprover = nikApprover;
                jbtnApprover = "";
            }
        }
        switch (jbtnApprover) {
            case "Kabid Keperawatan": urutanApprover = 1; break;
            case "Kabid Yanmed":      urutanApprover = 2; break;
            case "Kabag Umum & SDM":  urutanApprover = 3; break;
            case "Direktur":          urutanApprover = 4; break;
            default:                  urutanApprover = 0; break;
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));

        // Header
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1));
        pnlHeader.setBackground(new Color(0, 90, 150));
        pnlHeader.setPreferredSize(new Dimension(0, 52));
        JLabel lblJudul = new JLabel("   APPROVAL PERBAIKAN DATA REKAM MEDIS");
        lblJudul.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblJudul.setForeground(Color.WHITE);
        String infoLevel = urutanApprover > 0
            ? "Urutan ke-" + urutanApprover : "Tidak memiliki akses approval";
        JLabel lblInfo = new JLabel("   Login sebagai: " + namaApprover
            + "  |  Jabatan: " + jbtnApprover + "  |  Level: " + infoLevel);
        lblInfo.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblInfo.setForeground(new Color(190, 220, 255));
        pnlHeader.add(lblJudul);
        pnlHeader.add(lblInfo);
        add(pnlHeader, BorderLayout.NORTH);

        // Tidak punya akses
        if (urutanApprover == 0) {
            JPanel pnlTidak = new JPanel(new BorderLayout());
            pnlTidak.setBackground(Color.WHITE);
            JLabel lblTidak = new JLabel(
                "<html><center><br><br><br>"
                + "<span style='font-size:14px;color:red;'><b>Akses Ditolak</b></span><br><br>"
                + "Halaman ini hanya dapat diakses oleh:<br>"
                + "<b>Kabid Keperawatan &nbsp;|&nbsp; Kabid Yanmed</b><br>"
                + "<b>Kabag Umum &amp; SDM &nbsp;|&nbsp; Direktur</b>"
                + "</center></html>", JLabel.CENTER);
            lblTidak.setFont(new Font("Tahoma", Font.PLAIN, 12));
            pnlTidak.add(lblTidak, BorderLayout.CENTER);
            JPanel pnlBtn2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnTutup2 = new JButton("  Tutup  ");
            btnTutup2.addActionListener(e -> dispose());
            pnlBtn2.add(btnTutup2);
            pnlTidak.add(pnlBtn2, BorderLayout.SOUTH);
            add(pnlTidak, BorderLayout.CENTER);
            return;
        }

        // Tabel
        String[] kolom = {"ID", "No. RM", "Nama Pasien", "Bagian Data",
                          "Jenis Layanan", "Tgl Pengajuan", "Pengaju", "Status Saya"};
        modelPengajuan = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPengajuan = new JTable(modelPengajuan);
        tblPengajuan.setRowHeight(22);
        tblPengajuan.setFont(new Font("Tahoma", Font.PLAIN, 11));
        tblPengajuan.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        tblPengajuan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPengajuan.getColumnModel().getColumn(0).setMaxWidth(45);
        tblPengajuan.getColumnModel().getColumn(4).setMaxWidth(100);
        tblPengajuan.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String st = t.getValueAt(r, 7) != null ? t.getValueAt(r, 7).toString() : "";
                    if ("Disetujui".equals(st)) setBackground(new Color(220, 255, 220));
                    else if ("Ditolak".equals(st)) setBackground(new Color(255, 220, 220));
                    else setBackground(new Color(255, 255, 220));
                }
                return this;
            }
        });

        JPanel pnlTabel = new JPanel(new BorderLayout(3, 3));
        pnlTabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        JLabel lblTitle = new JLabel("Daftar Pengajuan Menunggu Approval  ("
            + jbtnApprover + " - Level " + urutanApprover + ")");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        pnlTabel.add(lblTitle, BorderLayout.NORTH);
        pnlTabel.add(new JScrollPane(tblPengajuan), BorderLayout.CENTER);
        JLabel lblKet = new JLabel("  Kuning = Menunggu  |  Hijau = Disetujui  |  Merah = Ditolak");
        lblKet.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblKet.setForeground(Color.GRAY);
        pnlTabel.add(lblKet, BorderLayout.SOUTH);
        add(pnlTabel, BorderLayout.CENTER);

        // Tombol
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        pnlBtn.setBackground(new Color(245, 245, 245));
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadPengajuan());
        btnDetail = new JButton("Lihat Detail");
        btnDetail.addActionListener(e -> lihatDetail());
        btnSetujui = new JButton("   Setujui   ");
        btnSetujui.setBackground(new Color(34, 120, 34));
        btnSetujui.setForeground(Color.WHITE);
        btnSetujui.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnSetujui.addActionListener(e -> prosesApproval(true));
        btnTolak = new JButton("   Tolak   ");
        btnTolak.setBackground(new Color(180, 30, 30));
        btnTolak.setForeground(Color.WHITE);
        btnTolak.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnTolak.addActionListener(e -> prosesApproval(false));
        JButton btnTutup = new JButton("Tutup");
        btnTutup.addActionListener(e -> dispose());
        pnlBtn.add(btnRefresh);
        pnlBtn.add(btnDetail);
        pnlBtn.add(Box.createHorizontalStrut(30));
        pnlBtn.add(btnSetujui);
        pnlBtn.add(btnTolak);
        pnlBtn.add(Box.createHorizontalStrut(30));
        pnlBtn.add(btnTutup);
        add(pnlBtn, BorderLayout.SOUTH);
    }

    private void loadPengajuan() {
        if (urutanApprover == 0) return;
        modelPengajuan.setRowCount(0);
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                "SELECT k.id, k.no_rm, k.nama_pasien, k.bagian_data, k.jenis_layanan, "
                + "DATE_FORMAT(k.tgl_pengajuan,'%d-%m-%Y %H:%i') as tgl, "
                + "k.pengaju_nama, a.status as status_saya "
                + "FROM koreksi_rm k "
                + "JOIN koreksi_rm_approval a ON k.id = a.koreksi_id "
                + "WHERE a.urutan = ? AND k.status = 'Menunggu Persetujuan' "
                + "ORDER BY k.tgl_pengajuan ASC");
            ps.setInt(1, urutanApprover);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int koreksiId = rs.getInt("id");
                if (urutanApprover > 1) {
                    PreparedStatement psCek = koneksi.prepareStatement(
                        "SELECT status FROM koreksi_rm_approval "
                        + "WHERE koreksi_id = ? AND urutan = ?");
                    psCek.setInt(1, koreksiId);
                    psCek.setInt(2, urutanApprover - 1);
                    ResultSet rsCek = psCek.executeQuery();
                    boolean prev = rsCek.next() && "Disetujui".equals(rsCek.getString("status"));
                    rsCek.close(); psCek.close();
                    if (!prev) continue;
                }
                modelPengajuan.addRow(new Object[]{
                    koreksiId, rs.getString("no_rm"), rs.getString("nama_pasien"),
                    rs.getString("bagian_data"), rs.getString("jenis_layanan"),
                    rs.getString("tgl"), rs.getString("pengaju_nama"), rs.getString("status_saya")
                });
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void prosesApproval(boolean disetujui) {
        int row = tblPengajuan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pengajuan terlebih dahulu!"); return;
        }
        String statusSaya = modelPengajuan.getValueAt(row, 7) != null
            ? modelPengajuan.getValueAt(row, 7).toString() : "";
        if (!"Menunggu".equals(statusSaya)) {
            JOptionPane.showMessageDialog(this,
                "Pengajuan ini sudah diproses.\nStatus: " + statusSaya); return;
        }
        int koreksiId = (int) modelPengajuan.getValueAt(row, 0);
        String namaPasien = modelPengajuan.getValueAt(row, 2).toString();
        String catatan = "";
        if (!disetujui) {
            catatan = JOptionPane.showInputDialog(this,
                "Alasan penolakan untuk: " + namaPasien, "Alasan Penolakan",
                JOptionPane.QUESTION_MESSAGE);
            if (catatan == null || catatan.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alasan penolakan wajib diisi!"); return;
            }
        }
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Yakin akan " + (disetujui ? "menyetujui" : "menolak")
            + " pengajuan pasien: " + namaPasien + " ?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;
        try {
            String statusBaru = disetujui ? "Disetujui" : "Ditolak";
            PreparedStatement ps = koneksi.prepareStatement(
                "UPDATE koreksi_rm_approval SET status=?, approver_nik=?, "
                + "approver_nama=?, tgl_aksi=NOW(), catatan=? "
                + "WHERE koreksi_id=? AND urutan=?");
            ps.setString(1, statusBaru);
            ps.setString(2, nikApprover);
            ps.setString(3, namaApprover);
            ps.setString(4, catatan.trim());
            ps.setInt(5, koreksiId);
            ps.setInt(6, urutanApprover);
            ps.executeUpdate(); ps.close();

            if (!disetujui) {
                PreparedStatement ps2 = koneksi.prepareStatement(
                    "UPDATE koreksi_rm SET status='Ditolak', keterangan_penolakan=? WHERE id=?");
                ps2.setString(1, "[" + jbtnApprover + "] " + catatan.trim());
                ps2.setInt(2, koreksiId);
                ps2.executeUpdate(); ps2.close();
            } else if (urutanApprover == 4) {
                PreparedStatement ps3 = koneksi.prepareStatement(
                    "UPDATE koreksi_rm SET status='Disetujui' WHERE id=?");
                ps3.setInt(1, koreksiId);
                ps3.executeUpdate(); ps3.close();
            }

            JOptionPane.showMessageDialog(this,
                "Pengajuan berhasil di" + (disetujui ? "setujui" : "tolak") + "!",
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            loadPengajuan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal proses: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lihatDetail() {
        int row = tblPengajuan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pengajuan terlebih dahulu!"); return;
        }
        int id = (int) modelPengajuan.getValueAt(row, 0);
        new RMDetailKoreksiRM(null, true, id).setVisible(true);
    }
}