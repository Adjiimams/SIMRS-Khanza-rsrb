package rekammedis;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import fungsi.koneksiDB;

/**
 * Viewer hasil laboratorium (Patologi Klinis & Patologi Anatomi) untuk 1 no_rawat.
 * Dialog ini SENGAJA dibuat non-modal ketika dipanggil dari form lain (lihat
 * cara pemanggilan di RMCatatanAnastesiSedasi.java, tombol "Lihat Hasil Lab"),
 * supaya form pemanggil tidak perlu ditutup/kehilangan data yang sedang diisi.
 *
 * Read-only murni - tidak ada fungsi simpan/ubah/hapus di sini.
 */
public class RMHasilLaboratorium extends JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private String noRawat;

    private JTable tbLabKlinis;
    private JTable tbLabPA;
    private DefaultTableModel tabModeLabKlinis;
    private DefaultTableModel tabModeLabPA;
    private JLabel lblInfo;

    public RMHasilLaboratorium(java.awt.Frame parent, boolean modal, String noRawat, String namaPasien) {
        super(parent, modal);
        this.noRawat = noRawat;
        initComponents();
        setTitle("Hasil Laboratorium - " + (namaPasien==null?"":namaPasien) + " (" + noRawat + ")");
        lblInfo.setText("No.Rawat : " + noRawat + "   |   Pasien : " + (namaPasien==null?"-":namaPasien));
        tampilLabKlinis();
        tampilLabPA();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
        lblInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(lblInfo, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // ===== Tab 1: Patologi Klinis =====
        tabModeLabKlinis = new DefaultTableModel(null, new Object[]{
            "Tanggal", "Jam", "Pemeriksaan", "Hasil", "Nilai Rujukan", "Keterangan"
        }) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tbLabKlinis = new JTable(tabModeLabKlinis);
        tbLabKlinis.setRowHeight(24);
        atur7LebarKolomKlinis();
        JScrollPane scrollKlinis = new JScrollPane(tbLabKlinis);
        tabbedPane.addTab("Patologi Klinis", scrollKlinis);

        // ===== Tab 2: Patologi Anatomi =====
        tabModeLabPA = new DefaultTableModel(null, new Object[]{
            "Tanggal", "Jam", "Pemeriksaan", "Diagnosa Klinik", "Makroskopik", "Mikroskopik", "Kesimpulan", "Kesan"
        }) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tbLabPA = new JTable(tabModeLabPA);
        tbLabPA.setRowHeight(24);
        JScrollPane scrollPA = new JScrollPane(tbLabPA);
        tabbedPane.addTab("Patologi Anatomi", scrollPA);

        add(tabbedPane, BorderLayout.CENTER);

        setSize(950, 500);
        setLocationRelativeTo(null);
    }

    private void atur7LebarKolomKlinis() {
        tbLabKlinis.getColumnModel().getColumn(0).setPreferredWidth(90);
        tbLabKlinis.getColumnModel().getColumn(1).setPreferredWidth(70);
        tbLabKlinis.getColumnModel().getColumn(2).setPreferredWidth(220);
        tbLabKlinis.getColumnModel().getColumn(3).setPreferredWidth(120);
        tbLabKlinis.getColumnModel().getColumn(4).setPreferredWidth(120);
        tbLabKlinis.getColumnModel().getColumn(5).setPreferredWidth(150);
    }

    private void tampilLabKlinis() {
        tabModeLabKlinis.setRowCount(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = koneksi.prepareStatement(
                    "select detail_periksa_lab.tgl_periksa, detail_periksa_lab.jam, "+
                    "jns_perawatan_lab.nm_perawatan, detail_periksa_lab.nilai, "+
                    "detail_periksa_lab.nilai_rujukan, detail_periksa_lab.keterangan "+
                    "from detail_periksa_lab "+
                    "inner join jns_perawatan_lab on detail_periksa_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "+
                    "where detail_periksa_lab.no_rawat=? "+
                    "order by detail_periksa_lab.tgl_periksa desc, detail_periksa_lab.jam desc");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeLabKlinis.addRow(new Object[]{
                    rs.getDate("tgl_periksa"),
                    rs.getString("jam"),
                    rs.getString("nm_perawatan"),
                    rs.getString("nilai"),
                    rs.getString("nilai_rujukan"),
                    rs.getString("keterangan")
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    private void tampilLabPA() {
        tabModeLabPA.setRowCount(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = koneksi.prepareStatement(
                    "select detail_periksa_labpa.tgl_periksa, detail_periksa_labpa.jam, "+
                    "jns_perawatan_lab.nm_perawatan, detail_periksa_labpa.diagnosa_klinik, "+
                    "detail_periksa_labpa.makroskopik, detail_periksa_labpa.mikroskopik, "+
                    "detail_periksa_labpa.kesimpulan, detail_periksa_labpa.kesan "+
                    "from detail_periksa_labpa "+
                    "inner join jns_perawatan_lab on detail_periksa_labpa.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw "+
                    "where detail_periksa_labpa.no_rawat=? "+
                    "order by detail_periksa_labpa.tgl_periksa desc, detail_periksa_labpa.jam desc");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeLabPA.addRow(new Object[]{
                    rs.getDate("tgl_periksa"),
                    rs.getString("jam"),
                    rs.getString("nm_perawatan"),
                    rs.getString("diagnosa_klinik"),
                    rs.getString("makroskopik"),
                    rs.getString("mikroskopik"),
                    rs.getString("kesimpulan"),
                    rs.getString("kesan")
                });
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }
}
