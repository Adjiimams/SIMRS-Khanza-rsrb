/*
 * Kontribusi Agus Budiyono Puskesmas Kerjo
 */


package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.SwingUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPetugas;


/**
 *
 * @author perpustakaan
 */
public final class RMSkriningKecanduanAlkohol extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariPetugas petugas;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private String finger="";
    private StringBuilder htmlContent;
    private String TANGGALMUNDUR="yes";
    //private DlgCariSkalaMotivasi motivasi=new DlgCariSkalaMotivasi(null,false);
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMSkriningKecanduanAlkohol(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien","Tgl.Lahir","Umur","Kode Petugas","Nama Petugas","Tanggal",
            "Frekuensi Minum","N1","Jumlah Minum/Hari","N2","Frekuensi Minum Berlebih","N3","Tidak Bisa Berhenti","N4",
            "Gagal Tanggung Jawab","N5","Minum Pagi Hari","N6","Rasa Bersalah","N7","Lupa Kejadian","N8",
            "Cedera Akibat Minum","N9","Saran Mengurangi","N10","N.Total","Kesimpulan","Rekomendasi Tindak Lanjut"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 31; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(40);
            }else if(i==5){
                column.setPreferredWidth(90);
            }else if(i==6){
                column.setPreferredWidth(150);
            }else if(i==7){
                column.setPreferredWidth(115);
            }else if(i==8){
                column.setPreferredWidth(80);
            }else if(i==9){
                column.setPreferredWidth(40);
            }else if(i==10){
                column.setPreferredWidth(100);
            }else if(i==11){
                column.setPreferredWidth(40);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(40);
            }else if(i==14){
                column.setPreferredWidth(75);
            }else if(i==15){
                column.setPreferredWidth(40);
            }else if(i==16){
                column.setPreferredWidth(100);
            }else if(i==17){
                column.setPreferredWidth(40);
            }else if(i==18){
                column.setPreferredWidth(90);
            }else if(i==19){
                column.setPreferredWidth(40);
            }else if(i==20){
                column.setPreferredWidth(90);
            }else if(i==21){
                column.setPreferredWidth(40);
            }else if(i==22){
                column.setPreferredWidth(90);
            }else if(i==23){
                column.setPreferredWidth(40);
            }else if(i==24){
                column.setPreferredWidth(150);
            }else if(i==25){
                column.setPreferredWidth(40);
            }else if(i==26){
                column.setPreferredWidth(150);
            }else if(i==27){
                column.setPreferredWidth(40);
            }else if(i==28){
                column.setPreferredWidth(45);
            }else if(i==29){
                column.setPreferredWidth(150);
            }else if(i==30){
                column.setPreferredWidth(300);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte)20).getKata(KdPetugas));
        Kesimpulan.setDocument(new batasInput((byte)50).getKata(Kesimpulan));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
        ChkInput.setSelected(false);
        isForm();
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
        
        try {
            TANGGALMUNDUR=koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR="yes";
        }
        
        jam();
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnSkriningKecanduanAlkohol = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        LoadHTML = new widget.editorpane();
        Umur = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel16 = new widget.Label();
        Tanggal = new widget.Tanggal();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        BtnPetugas = new widget.Button();
        jLabel99 = new widget.Label();
        jLabel75 = new widget.Label();
        jLabel76 = new widget.Label();
        RokokDihisab = new widget.ComboBox();
        jLabel92 = new widget.Label();
        NilaiRokokDihisab = new widget.TextBox();
        jLabel77 = new widget.Label();
        jLabel78 = new widget.Label();
        MenyalakanRokok = new widget.ComboBox();
        jLabel69 = new widget.Label();
        NilaiMenyalakanRokok = new widget.TextBox();
        jLabel96 = new widget.Label();
        jLabel95 = new widget.Label();
        TidakRela = new widget.ComboBox();
        jLabel97 = new widget.Label();
        NilaiTidakRela = new widget.TextBox();
        jLabel107 = new widget.Label();
        jLabel108 = new widget.Label();
        JamPertama = new widget.ComboBox();
        jLabel109 = new widget.Label();
        NilaiJamPertama = new widget.TextBox();
        jLabel111 = new widget.Label();
        jLabel110 = new widget.Label();
        jLabel113 = new widget.Label();
        RasaIngin = new widget.ComboBox();
        jLabel112 = new widget.Label();
        NilaiRasaIngin = new widget.TextBox();
        jLabel114 = new widget.Label();
        jLabel115 = new widget.Label();
        jLabel118 = new widget.Label();
        SakitBerat = new widget.ComboBox();
        jLabel116 = new widget.Label();
        NilaiSakitBerat = new widget.TextBox();
        jLabel200 = new widget.Label();
        jLabel201 = new widget.Label();
        jLabel202 = new widget.Label();
        Q7 = new widget.ComboBox();
        jLabel203 = new widget.Label();
        NilaiQ7 = new widget.TextBox();
        jLabel204 = new widget.Label();
        jLabel205 = new widget.Label();
        jLabel206 = new widget.Label();
        Q8 = new widget.ComboBox();
        jLabel207 = new widget.Label();
        NilaiQ8 = new widget.TextBox();
        jLabel208 = new widget.Label();
        jLabel209 = new widget.Label();
        Q9 = new widget.ComboBox();
        jLabel210 = new widget.Label();
        NilaiQ9 = new widget.TextBox();
        jLabel211 = new widget.Label();
        jLabel212 = new widget.Label();
        Q10 = new widget.ComboBox();
        jLabel213 = new widget.Label();
        NilaiQ10 = new widget.TextBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel148 = new widget.Label();
        jLabel149 = new widget.Label();
        Kesimpulan = new widget.TextBox();
        jLabel73 = new widget.Label();
        TotalNilai = new widget.TextBox();
        jLabel150 = new widget.Label();
        jLabel152 = new widget.Label();
        SkalaMotivasi = new widget.TextBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel101 = new widget.Label();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnSkriningKecanduanAlkohol.setBackground(new java.awt.Color(255, 255, 254));
        MnSkriningKecanduanAlkohol.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSkriningKecanduanAlkohol.setForeground(new java.awt.Color(50, 50, 50));
        MnSkriningKecanduanAlkohol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSkriningKecanduanAlkohol.setText("Formulir Skrining Kecanduan Alkohol (AUDIT-WHO)");
        MnSkriningKecanduanAlkohol.setName("MnSkriningKecanduanAlkohol"); // NOI18N
        MnSkriningKecanduanAlkohol.setPreferredSize(new java.awt.Dimension(280, 26));
        MnSkriningKecanduanAlkohol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSkriningKecanduanAlkoholActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSkriningKecanduanAlkohol);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        Umur.setEditable(false);
        Umur.setFocusTraversalPolicyProvider(true);
        Umur.setName("Umur"); // NOI18N

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Skrining Kecanduan Alkohol AUDIT (Alcohol Use Disorders Identification Test - WHO) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setPreferredSize(new java.awt.Dimension(462, 849));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-02-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-02-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 375));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 380));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 460));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 75, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 141, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 75, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-02-2026" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(79, 40, 90, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(173, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(238, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(303, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(368, 40, 23, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 70, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setHighlighter(null);
        KdPetugas.setName("KdPetugas"); // NOI18N
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(474, 40, 94, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(570, 40, 187, 23);

        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPetugas.setMnemonic('2');
        BtnPetugas.setToolTipText("ALt+2");
        BtnPetugas.setName("BtnPetugas"); // NOI18N
        BtnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPetugasActionPerformed(evt);
            }
        });
        BtnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPetugasKeyPressed(evt);
            }
        });
        FormInput.add(BtnPetugas);
        BtnPetugas.setBounds(761, 40, 28, 23);

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel99.setText("I. ANAMNESIS");
        jLabel99.setName("jLabel99"); // NOI18N
        FormInput.add(jLabel99);
        jLabel99.setBounds(11, 70, 290, 23);

        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel75.setText("1.");
        jLabel75.setName("jLabel75"); // NOI18N
        FormInput.add(jLabel75);
        jLabel75.setBounds(44, 90, 20, 23);

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel76.setText("Seberapa sering Anda minum minuman yang mengandung alkohol ?");
        jLabel76.setName("jLabel76"); // NOI18N
        FormInput.add(jLabel76);
        jLabel76.setBounds(62, 90, 300, 23);

        RokokDihisab.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<=1x Sebulan", "2-4x Sebulan", "2-3x Seminggu", ">=4x Seminggu" }));
        RokokDihisab.setName("RokokDihisab"); // NOI18N
        RokokDihisab.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RokokDihisabItemStateChanged(evt);
            }
        });
        RokokDihisab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RokokDihisabKeyPressed(evt);
            }
        });
        FormInput.add(RokokDihisab);
        RokokDihisab.setBounds(610, 90, 90, 23);

        jLabel92.setText("Skor :");
        jLabel92.setName("jLabel92"); // NOI18N
        FormInput.add(jLabel92);
        jLabel92.setBounds(700, 90, 40, 23);

        NilaiRokokDihisab.setEditable(false);
        NilaiRokokDihisab.setText("0");
        NilaiRokokDihisab.setFocusTraversalPolicyProvider(true);
        NilaiRokokDihisab.setName("NilaiRokokDihisab"); // NOI18N
        FormInput.add(NilaiRokokDihisab);
        NilaiRokokDihisab.setBounds(744, 90, 45, 23);

        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel77.setText("2.");
        jLabel77.setName("jLabel77"); // NOI18N
        FormInput.add(jLabel77);
        jLabel77.setBounds(44, 120, 20, 23);

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel78.setText("Berapa banyak gelas standar minuman beralkohol yang biasa Anda minum dalam sehari ?");
        jLabel78.setName("jLabel78"); // NOI18N
        FormInput.add(jLabel78);
        jLabel78.setBounds(62, 120, 370, 23);

        MenyalakanRokok.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 atau 2 Gelas", "3 atau 4 Gelas", "5 atau 6 Gelas", "7 Hingga 9 Gelas", ">=10 Gelas" }));
        MenyalakanRokok.setSelectedIndex(3);
        MenyalakanRokok.setName("MenyalakanRokok"); // NOI18N
        MenyalakanRokok.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                MenyalakanRokokItemStateChanged(evt);
            }
        });
        MenyalakanRokok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenyalakanRokokKeyPressed(evt);
            }
        });
        FormInput.add(MenyalakanRokok);
        MenyalakanRokok.setBounds(555, 120, 145, 23);

        jLabel69.setText("Skor :");
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput.add(jLabel69);
        jLabel69.setBounds(700, 120, 40, 23);

        NilaiMenyalakanRokok.setEditable(false);
        NilaiMenyalakanRokok.setText("0");
        NilaiMenyalakanRokok.setFocusTraversalPolicyProvider(true);
        NilaiMenyalakanRokok.setName("NilaiMenyalakanRokok"); // NOI18N
        FormInput.add(NilaiMenyalakanRokok);
        NilaiMenyalakanRokok.setBounds(744, 120, 45, 23);

        jLabel96.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel96.setText("3.");
        jLabel96.setName("jLabel96"); // NOI18N
        FormInput.add(jLabel96);
        jLabel96.setBounds(44, 150, 20, 23);

        jLabel95.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel95.setText("Seberapa sering Anda minum 6 gelas standar atau lebih dalam satu kesempatan ?");
        jLabel95.setName("jLabel95"); // NOI18N
        FormInput.add(jLabel95);
        jLabel95.setBounds(62, 150, 370, 23);

        TidakRela.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<1x Sebulan", "1x Sebulan", "1x Seminggu", "Tiap Hari" }));
        TidakRela.setName("TidakRela"); // NOI18N
        TidakRela.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TidakRelaItemStateChanged(evt);
            }
        });
        TidakRela.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TidakRelaKeyPressed(evt);
            }
        });
        FormInput.add(TidakRela);
        TidakRela.setBounds(500, 150, 200, 23);

        jLabel97.setText("Skor :");
        jLabel97.setName("jLabel97"); // NOI18N
        FormInput.add(jLabel97);
        jLabel97.setBounds(700, 150, 40, 23);

        NilaiTidakRela.setEditable(false);
        NilaiTidakRela.setText("0");
        NilaiTidakRela.setFocusTraversalPolicyProvider(true);
        NilaiTidakRela.setName("NilaiTidakRela"); // NOI18N
        FormInput.add(NilaiTidakRela);
        NilaiTidakRela.setBounds(744, 150, 45, 23);

        jLabel107.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel107.setText("4.");
        jLabel107.setName("jLabel107"); // NOI18N
        FormInput.add(jLabel107);
        jLabel107.setBounds(44, 180, 20, 23);

        jLabel108.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel108.setText("Setahun terakhir, seberapa sering Anda tidak bisa berhenti minum setelah mulai ?");
        jLabel108.setName("jLabel108"); // NOI18N
        FormInput.add(jLabel108);
        jLabel108.setBounds(62, 180, 470, 23);

        JamPertama.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<1x Sebulan", "1x Sebulan", "1x Seminggu", "Tiap Hari" }));
        JamPertama.setName("JamPertama"); // NOI18N
        JamPertama.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                JamPertamaItemStateChanged(evt);
            }
        });
        JamPertama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamPertamaKeyPressed(evt);
            }
        });
        FormInput.add(JamPertama);
        JamPertama.setBounds(610, 180, 90, 23);

        jLabel109.setText("Skor :");
        jLabel109.setName("jLabel109"); // NOI18N
        FormInput.add(jLabel109);
        jLabel109.setBounds(700, 180, 40, 23);

        NilaiJamPertama.setEditable(false);
        NilaiJamPertama.setText("0");
        NilaiJamPertama.setFocusTraversalPolicyProvider(true);
        NilaiJamPertama.setName("NilaiJamPertama"); // NOI18N
        FormInput.add(NilaiJamPertama);
        NilaiJamPertama.setBounds(744, 180, 45, 23);

        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel111.setText("5.");
        jLabel111.setName("jLabel111"); // NOI18N
        FormInput.add(jLabel111);
        jLabel111.setBounds(44, 210, 20, 23);

        jLabel110.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel110.setText("Setahun terakhir, seberapa sering Anda gagal memenuhi tanggung jawab yang biasa diharapkan");
        jLabel110.setName("jLabel110"); // NOI18N
        FormInput.add(jLabel110);
        jLabel110.setBounds(62, 205, 550, 23);

        jLabel113.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel113.setText("dari Anda karena minum ?");
        jLabel113.setName("jLabel113"); // NOI18N
        FormInput.add(jLabel113);
        jLabel113.setBounds(62, 217, 530, 23);

        RasaIngin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<1x Sebulan", "1x Sebulan", "1x Seminggu", "Tiap Hari" }));
        RasaIngin.setName("RasaIngin"); // NOI18N
        RasaIngin.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                RasaInginItemStateChanged(evt);
            }
        });
        RasaIngin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RasaInginKeyPressed(evt);
            }
        });
        FormInput.add(RasaIngin);
        RasaIngin.setBounds(610, 210, 90, 23);

        jLabel112.setText("Skor :");
        jLabel112.setName("jLabel112"); // NOI18N
        FormInput.add(jLabel112);
        jLabel112.setBounds(700, 210, 40, 23);

        NilaiRasaIngin.setEditable(false);
        NilaiRasaIngin.setText("0");
        NilaiRasaIngin.setFocusTraversalPolicyProvider(true);
        NilaiRasaIngin.setName("NilaiRasaIngin"); // NOI18N
        FormInput.add(NilaiRasaIngin);
        NilaiRasaIngin.setBounds(744, 210, 45, 23);

        jLabel114.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel114.setText("6.");
        jLabel114.setName("jLabel114"); // NOI18N
        FormInput.add(jLabel114);
        jLabel114.setBounds(44, 240, 20, 23);

        jLabel115.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel115.setText("Setahun terakhir, seberapa sering Anda butuh minum di pagi hari untuk memulai aktivitas");
        jLabel115.setName("jLabel115"); // NOI18N
        FormInput.add(jLabel115);
        jLabel115.setBounds(62, 235, 440, 23);

        jLabel118.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel118.setText("setelah minum berat sebelumnya ?");
        jLabel118.setName("jLabel118"); // NOI18N
        FormInput.add(jLabel118);
        jLabel118.setBounds(62, 247, 530, 23);

        SakitBerat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<1x Sebulan", "1x Sebulan", "1x Seminggu", "Tiap Hari" }));
        SakitBerat.setName("SakitBerat"); // NOI18N
        SakitBerat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SakitBeratItemStateChanged(evt);
            }
        });
        SakitBerat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SakitBeratKeyPressed(evt);
            }
        });
        FormInput.add(SakitBerat);
        SakitBerat.setBounds(610, 240, 90, 23);

        jLabel116.setText("Skor :");
        jLabel116.setName("jLabel116"); // NOI18N
        FormInput.add(jLabel116);
        jLabel116.setBounds(700, 240, 40, 23);

        NilaiSakitBerat.setEditable(false);
        NilaiSakitBerat.setText("0");
        NilaiSakitBerat.setFocusTraversalPolicyProvider(true);
        NilaiSakitBerat.setName("NilaiSakitBerat"); // NOI18N
        FormInput.add(NilaiSakitBerat);
        NilaiSakitBerat.setBounds(744, 240, 45, 23);

        jLabel200.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel200.setText("7.");
        jLabel200.setName("jLabel200"); // NOI18N
        FormInput.add(jLabel200);
        jLabel200.setBounds(44, 270, 20, 23);

        jLabel201.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel201.setText("Setahun terakhir, seberapa sering Anda mengalami rasa bersalah atau menyesal");
        jLabel201.setName("jLabel201"); // NOI18N
        FormInput.add(jLabel201);
        jLabel201.setBounds(62, 265, 550, 23);

        jLabel202.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel202.setText("setelah minum ?");
        jLabel202.setName("jLabel202"); // NOI18N
        FormInput.add(jLabel202);
        jLabel202.setBounds(62, 277, 530, 23);

        Q7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<1x Sebulan", "1x Sebulan", "1x Seminggu", "Tiap Hari" }));
        Q7.setName("Q7"); // NOI18N
        Q7.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Q7ItemStateChanged(evt);
            }
        });
        Q7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Q7KeyPressed(evt);
            }
        });
        FormInput.add(Q7);
        Q7.setBounds(610, 270, 90, 23);

        jLabel203.setText("Skor :");
        jLabel203.setName("jLabel203"); // NOI18N
        FormInput.add(jLabel203);
        jLabel203.setBounds(700, 270, 40, 23);

        NilaiQ7.setEditable(false);
        NilaiQ7.setText("0");
        NilaiQ7.setFocusTraversalPolicyProvider(true);
        NilaiQ7.setName("NilaiQ7"); // NOI18N
        FormInput.add(NilaiQ7);
        NilaiQ7.setBounds(744, 270, 45, 23);

        jLabel204.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel204.setText("8.");
        jLabel204.setName("jLabel204"); // NOI18N
        FormInput.add(jLabel204);
        jLabel204.setBounds(44, 300, 20, 23);

        jLabel205.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel205.setText("Setahun terakhir, seberapa sering Anda tidak ingat kejadian malam sebelumnya");
        jLabel205.setName("jLabel205"); // NOI18N
        FormInput.add(jLabel205);
        jLabel205.setBounds(62, 295, 550, 23);

        jLabel206.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel206.setText("karena minum ?");
        jLabel206.setName("jLabel206"); // NOI18N
        FormInput.add(jLabel206);
        jLabel206.setBounds(62, 307, 530, 23);

        Q8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Pernah", "<1x Sebulan", "1x Sebulan", "1x Seminggu", "Tiap Hari" }));
        Q8.setName("Q8"); // NOI18N
        Q8.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Q8ItemStateChanged(evt);
            }
        });
        Q8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Q8KeyPressed(evt);
            }
        });
        FormInput.add(Q8);
        Q8.setBounds(610, 300, 90, 23);

        jLabel207.setText("Skor :");
        jLabel207.setName("jLabel207"); // NOI18N
        FormInput.add(jLabel207);
        jLabel207.setBounds(700, 300, 40, 23);

        NilaiQ8.setEditable(false);
        NilaiQ8.setText("0");
        NilaiQ8.setFocusTraversalPolicyProvider(true);
        NilaiQ8.setName("NilaiQ8"); // NOI18N
        FormInput.add(NilaiQ8);
        NilaiQ8.setBounds(744, 300, 45, 23);

        jLabel208.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel208.setText("9.");
        jLabel208.setName("jLabel208"); // NOI18N
        FormInput.add(jLabel208);
        jLabel208.setBounds(44, 330, 20, 23);

        jLabel209.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel209.setText("Apakah Anda atau orang lain pernah cedera akibat Anda minum alkohol ?");
        jLabel209.setName("jLabel209"); // NOI18N
        FormInput.add(jLabel209);
        jLabel209.setBounds(62, 330, 500, 23);

        Q9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya, Bukan Setahun Ini", "Ya, Setahun Ini" }));
        Q9.setName("Q9"); // NOI18N
        Q9.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Q9ItemStateChanged(evt);
            }
        });
        Q9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Q9KeyPressed(evt);
            }
        });
        FormInput.add(Q9);
        Q9.setBounds(570, 330, 130, 23);

        jLabel210.setText("Skor :");
        jLabel210.setName("jLabel210"); // NOI18N
        FormInput.add(jLabel210);
        jLabel210.setBounds(700, 330, 40, 23);

        NilaiQ9.setEditable(false);
        NilaiQ9.setText("0");
        NilaiQ9.setFocusTraversalPolicyProvider(true);
        NilaiQ9.setName("NilaiQ9"); // NOI18N
        FormInput.add(NilaiQ9);
        NilaiQ9.setBounds(744, 330, 45, 23);

        jLabel211.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel211.setText("10.");
        jLabel211.setName("jLabel211"); // NOI18N
        FormInput.add(jLabel211);
        jLabel211.setBounds(44, 360, 20, 23);

        jLabel212.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel212.setText("Ada keluarga/teman/dokter yang khawatir atau menyarankan Anda mengurangi minum ?");
        jLabel212.setName("jLabel212"); // NOI18N
        FormInput.add(jLabel212);
        jLabel212.setBounds(62, 360, 500, 23);

        Q10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya, Bukan Setahun Ini", "Ya, Setahun Ini" }));
        Q10.setName("Q10"); // NOI18N
        Q10.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                Q10ItemStateChanged(evt);
            }
        });
        Q10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Q10KeyPressed(evt);
            }
        });
        FormInput.add(Q10);
        Q10.setBounds(570, 360, 130, 23);

        jLabel213.setText("Skor :");
        jLabel213.setName("jLabel213"); // NOI18N
        FormInput.add(jLabel213);
        jLabel213.setBounds(700, 360, 40, 23);

        NilaiQ10.setEditable(false);
        NilaiQ10.setText("0");
        NilaiQ10.setFocusTraversalPolicyProvider(true);
        NilaiQ10.setName("NilaiQ10"); // NOI18N
        FormInput.add(NilaiQ10);
        NilaiQ10.setBounds(744, 360, 45, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 807, 1);

        jLabel148.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel148.setText("Kesimpulan");
        jLabel148.setName("jLabel148"); // NOI18N
        FormInput.add(jLabel148);
        jLabel148.setBounds(44, 290, 80, 23);

        jLabel149.setText(":");
        jLabel149.setName("jLabel149"); // NOI18N
        FormInput.add(jLabel149);
        jLabel149.setBounds(0, 290, 108, 23);

        Kesimpulan.setEditable(false);
        Kesimpulan.setFocusTraversalPolicyProvider(true);
        Kesimpulan.setName("Kesimpulan"); // NOI18N
        Kesimpulan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KesimpulanKeyPressed(evt);
            }
        });
        FormInput.add(Kesimpulan);
        Kesimpulan.setBounds(112, 390, 500, 23);

        jLabel73.setText("Total Skor :");
        jLabel73.setName("jLabel73"); // NOI18N
        FormInput.add(jLabel73);
        jLabel73.setBounds(670, 290, 70, 23);

        TotalNilai.setEditable(false);
        TotalNilai.setText("0");
        TotalNilai.setFocusTraversalPolicyProvider(true);
        TotalNilai.setName("TotalNilai"); // NOI18N
        FormInput.add(TotalNilai);
        TotalNilai.setBounds(744, 390, 45, 23);

        jLabel150.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel150.setText("Rekomendasi");
        jLabel150.setName("jLabel150"); // NOI18N
        FormInput.add(jLabel150);
        jLabel150.setBounds(44, 420, 90, 23);

        jLabel152.setText(":");
        jLabel152.setName("jLabel152"); // NOI18N
        FormInput.add(jLabel152);
        jLabel152.setBounds(0, 420, 120, 23);

        SkalaMotivasi.setEditable(false);
        SkalaMotivasi.setText("Risiko Rendah - Edukasi Alkohol");
        SkalaMotivasi.setName("SkalaMotivasi"); // NOI18N
        SkalaMotivasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SkalaMotivasiKeyPressed(evt);
            }
        });
        FormInput.add(SkalaMotivasi);
        SkalaMotivasi.setBounds(124, 420, 665, 23);

        jSeparator3.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator3.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator3.setName("jSeparator3"); // NOI18N
        FormInput.add(jSeparator3);
        jSeparator3.setBounds(0, 270, 807, 1);

        jLabel101.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel101.setText("II. INTERPRETASI");
        jLabel101.setName("jLabel101"); // NOI18N
        FormInput.add(jLabel101);
        jLabel101.setBounds(10, 270, 200, 23);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
        }else{            
            Valid.pindah(evt,TCari,Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(KdPetugas.getText().trim().equals("")||NmPetugas.getText().trim().equals("")){
            Valid.textKosong(KdPetugas,"Petugas");
        }else{
            if(akses.getkode().equals("Admin Utama")){
                simpan();
            }else{
                if(TanggalRegistrasi.getText().equals("")){
                    TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                }
                if(Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem())==true){
                    simpan();
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,SkalaMotivasi,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm(); 
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else{
                if(KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString())){
                    if(Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString(),Sequel.ambiltanggalsekarang())==true){
                        hapus();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
        }  
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(KdPetugas.getText().trim().equals("")||NmPetugas.getText().trim().equals("")){
            Valid.textKosong(KdPetugas,"Petugas");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString())){
                        if(Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString(),Sequel.ambiltanggalsekarang())==true){
                            if(TanggalRegistrasi.getText().equals("")){
                                TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                            }
                            if(Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem())==true){
                                ganti();
                            }
                        }
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh petugas yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                htmlContent = new StringBuilder();
                htmlContent.append(                             
                    "<tr class='isi'>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl.Lahir</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Umur</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kode Petugas</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Petugas</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Rokok Dihisab</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.R.D</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Menyalakan Rokok</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.M.R</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Rokok Tidak Direlakan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.R.T</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jam Pertama</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.J.P</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kesulitan Menahan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.K.M</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Saat Sakit Berat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.S.B</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>N.Total</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kesimpulan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Skala Motivasi</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                           "<td valign='top'>"+tbObat.getValueAt(i,0).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,12).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,15).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,16).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,17).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,18).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,19).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,20).toString()+"</td>"+ 
                            "<td valign='top'>"+tbObat.getValueAt(i,21).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,22).toString()+"</td>"+
                            
                        "</tr>");
                }
                LoadHTML.setText(
                    "<html>"+
                      "<table width='1900px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>"
                );

                File g = new File("file2.css");            
                try (BufferedWriter bg = new BufferedWriter(new FileWriter(g))) {
                    bg.write(
                            ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                    );
                }

                File f = new File("DataSkriningRisikoKankerPayudara.html");            
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='1900px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA SKRINING RISIKO KECANDUAN ALKOHOL<br><br></font>"+        
                                    "</td>"+
                               "</tr>"+
                            "</table>")
                );
                bw.close();                         
                Desktop.getDesktop().browse(f.toURI());

            }catch(IOException e){
                System.out.println("Notifikasi : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                BtnCariActionPerformed(null);
                break;
            case KeyEvent.VK_PAGE_DOWN:
                BtnCari.requestFocus();
                break;
            case KeyEvent.VK_PAGE_UP:
                BtnKeluar.requestFocus();
                break;
            default:
                break;
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() ->tampil());
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        runBackground(() ->tampil());
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            runBackground(() ->tampil());
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        Valid.pindah(evt,TCari,Jam);
}//GEN-LAST:event_TanggalKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void JamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamKeyPressed
        Valid.pindah(evt,Tanggal,Menit);
    }//GEN-LAST:event_JamKeyPressed

    private void MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitKeyPressed
        Valid.pindah(evt,Jam,Detik);
    }//GEN-LAST:event_MenitKeyPressed

    private void DetikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikKeyPressed
        Valid.pindah(evt,Menit,BtnPetugas);
    }//GEN-LAST:event_DetikKeyPressed

    private void BtnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPetugasActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelectedRow()!= -1){                   
                        KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                    }  
                    BtnPetugas.requestFocus();
                    petugas=null;
                }
            });

            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
        if (petugas == null) return;
        if (!petugas.isVisible()) {
            petugas.isCek();    
            petugas.emptTeks();
        }
        
        if (petugas.isVisible()) {
            petugas.toFront();
            return;
        }
        petugas.setVisible(true); 
    }//GEN-LAST:event_BtnPetugasActionPerformed

    private void BtnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPetugasKeyPressed
        Valid.pindah(evt,TCari,RokokDihisab);
    }//GEN-LAST:event_BtnPetugasKeyPressed

    private void MnSkriningKecanduanAlkoholActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSkriningKecanduanAlkoholActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),5).toString():finger)+"\n"+Tanggal.getSelectedItem()); 
            Valid.MyReportqry("rptFormulirSkriningKecanduanAlkohol.jasper","report","::[ Formulir Skrining Kecanduan Alkohol (AUDIT-WHO) ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,reg_periksa.umurdaftar,reg_periksa.sttsumur,skrining_kecanduan_alkohol.nip,"+
                    "petugas.nama,skrining_kecanduan_alkohol.tanggal,skrining_kecanduan_alkohol.frekuensi_minum,skrining_kecanduan_alkohol.nilai_frekuensi_minum,skrining_kecanduan_alkohol.jumlah_minum,"+
                    "skrining_kecanduan_alkohol.nilai_jumlah_minum,skrining_kecanduan_alkohol.frekuensi_minum_berlebih,skrining_kecanduan_alkohol.nilai_frekuensi_minum_berlebih,skrining_kecanduan_alkohol.tidak_bisa_berhenti,"+
                    "skrining_kecanduan_alkohol.nilai_tidak_bisa_berhenti,skrining_kecanduan_alkohol.gagal_tanggung_jawab,skrining_kecanduan_alkohol.nilai_gagal_tanggung_jawab,skrining_kecanduan_alkohol.minum_pagi_hari,"+
                    "skrining_kecanduan_alkohol.nilai_minum_pagi_hari,skrining_kecanduan_alkohol.rasa_bersalah,skrining_kecanduan_alkohol.nilai_rasa_bersalah,skrining_kecanduan_alkohol.lupa_kejadian,skrining_kecanduan_alkohol.nilai_lupa_kejadian,"+
                    "skrining_kecanduan_alkohol.cedera_akibat_minum,skrining_kecanduan_alkohol.nilai_cedera_akibat_minum,skrining_kecanduan_alkohol.saran_mengurangi,skrining_kecanduan_alkohol.nilai_saran_mengurangi,"+
                    "skrining_kecanduan_alkohol.nilai_total,skrining_kecanduan_alkohol.keterangan_hasil_skrining,skrining_kecanduan_alkohol.rekomendasi_tindak_lanjut "+
                    "from skrining_kecanduan_alkohol inner join reg_periksa on skrining_kecanduan_alkohol.no_rawat=reg_periksa.no_rawat inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on skrining_kecanduan_alkohol.nip=petugas.nip where reg_periksa.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"'",param);
        }
    }//GEN-LAST:event_MnSkriningKecanduanAlkoholActionPerformed

    private void RokokDihisabItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RokokDihisabItemStateChanged
        switch (RokokDihisab.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiRokokDihisab.setText("0");
                break;
            case "<=1x Sebulan":
                NilaiRokokDihisab.setText("1");
                break;
            case "2-4x Sebulan":
                NilaiRokokDihisab.setText("2");
                break;
            case "2-3x Seminggu":
                NilaiRokokDihisab.setText("3");
                break;
            case ">=4x Seminggu":
                NilaiRokokDihisab.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_RokokDihisabItemStateChanged

    private void RokokDihisabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RokokDihisabKeyPressed
        Valid.pindah(evt,TCari,MenyalakanRokok);
    }//GEN-LAST:event_RokokDihisabKeyPressed

    private void MenyalakanRokokItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_MenyalakanRokokItemStateChanged
        switch (MenyalakanRokok.getSelectedItem().toString()) {
            case "1 atau 2 Gelas":
                NilaiMenyalakanRokok.setText("0");
                break;
            case "3 atau 4 Gelas":
                NilaiMenyalakanRokok.setText("1");
                break;
            case "5 atau 6 Gelas":
                NilaiMenyalakanRokok.setText("2");
                break;
            case "7 Hingga 9 Gelas":
                NilaiMenyalakanRokok.setText("3");
                break;
            case ">=10 Gelas":
                NilaiMenyalakanRokok.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_MenyalakanRokokItemStateChanged

    private void MenyalakanRokokKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenyalakanRokokKeyPressed
        Valid.pindah(evt,RokokDihisab,TidakRela);
    }//GEN-LAST:event_MenyalakanRokokKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void KesimpulanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KesimpulanKeyPressed
        //Valid.pindah(evt,Lapor,SG1);
    }//GEN-LAST:event_KesimpulanKeyPressed

    private void TidakRelaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TidakRelaItemStateChanged
        switch (TidakRela.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiTidakRela.setText("0");
                break;
            case "<1x Sebulan":
                NilaiTidakRela.setText("1");
                break;
            case "1x Sebulan":
                NilaiTidakRela.setText("2");
                break;
            case "1x Seminggu":
                NilaiTidakRela.setText("3");
                break;
            case "Tiap Hari":
                NilaiTidakRela.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_TidakRelaItemStateChanged

    private void TidakRelaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TidakRelaKeyPressed
        Valid.pindah(evt,MenyalakanRokok,JamPertama);
    }//GEN-LAST:event_TidakRelaKeyPressed

    private void JamPertamaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_JamPertamaItemStateChanged
        switch (JamPertama.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiJamPertama.setText("0");
                break;
            case "<1x Sebulan":
                NilaiJamPertama.setText("1");
                break;
            case "1x Sebulan":
                NilaiJamPertama.setText("2");
                break;
            case "1x Seminggu":
                NilaiJamPertama.setText("3");
                break;
            case "Tiap Hari":
                NilaiJamPertama.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_JamPertamaItemStateChanged

    private void JamPertamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamPertamaKeyPressed
        Valid.pindah(evt,TidakRela,RasaIngin);
    }//GEN-LAST:event_JamPertamaKeyPressed

    private void RasaInginItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_RasaInginItemStateChanged
        switch (RasaIngin.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiRasaIngin.setText("0");
                break;
            case "<1x Sebulan":
                NilaiRasaIngin.setText("1");
                break;
            case "1x Sebulan":
                NilaiRasaIngin.setText("2");
                break;
            case "1x Seminggu":
                NilaiRasaIngin.setText("3");
                break;
            case "Tiap Hari":
                NilaiRasaIngin.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_RasaInginItemStateChanged

    private void RasaInginKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RasaInginKeyPressed
        Valid.pindah(evt,JamPertama,SakitBerat);
    }//GEN-LAST:event_RasaInginKeyPressed

    private void SakitBeratItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SakitBeratItemStateChanged
        switch (SakitBerat.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiSakitBerat.setText("0");
                break;
            case "<1x Sebulan":
                NilaiSakitBerat.setText("1");
                break;
            case "1x Sebulan":
                NilaiSakitBerat.setText("2");
                break;
            case "1x Seminggu":
                NilaiSakitBerat.setText("3");
                break;
            case "Tiap Hari":
                NilaiSakitBerat.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_SakitBeratItemStateChanged

    private void SakitBeratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SakitBeratKeyPressed
        Valid.pindah(evt,RasaIngin,Q7);
    }//GEN-LAST:event_SakitBeratKeyPressed

    private void Q7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Q7ItemStateChanged
        switch (Q7.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiQ7.setText("0");
                break;
            case "<1x Sebulan":
                NilaiQ7.setText("1");
                break;
            case "1x Sebulan":
                NilaiQ7.setText("2");
                break;
            case "1x Seminggu":
                NilaiQ7.setText("3");
                break;
            case "Tiap Hari":
                NilaiQ7.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_Q7ItemStateChanged

    private void Q7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Q7KeyPressed
        Valid.pindah(evt,SakitBerat,Q8);
    }//GEN-LAST:event_Q7KeyPressed

    private void Q8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Q8ItemStateChanged
        switch (Q8.getSelectedItem().toString()) {
            case "Tidak Pernah":
                NilaiQ8.setText("0");
                break;
            case "<1x Sebulan":
                NilaiQ8.setText("1");
                break;
            case "1x Sebulan":
                NilaiQ8.setText("2");
                break;
            case "1x Seminggu":
                NilaiQ8.setText("3");
                break;
            case "Tiap Hari":
                NilaiQ8.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_Q8ItemStateChanged

    private void Q8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Q8KeyPressed
        Valid.pindah(evt,Q7,Q9);
    }//GEN-LAST:event_Q8KeyPressed

    private void Q9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Q9ItemStateChanged
        switch (Q9.getSelectedItem().toString()) {
            case "Tidak":
                NilaiQ9.setText("0");
                break;
            case "Ya, Bukan Setahun Ini":
                NilaiQ9.setText("2");
                break;
            case "Ya, Setahun Ini":
                NilaiQ9.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_Q9ItemStateChanged

    private void Q9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Q9KeyPressed
        Valid.pindah(evt,Q8,Q10);
    }//GEN-LAST:event_Q9KeyPressed

    private void Q10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_Q10ItemStateChanged
        switch (Q10.getSelectedItem().toString()) {
            case "Tidak":
                NilaiQ10.setText("0");
                break;
            case "Ya, Bukan Setahun Ini":
                NilaiQ10.setText("2");
                break;
            case "Ya, Setahun Ini":
                NilaiQ10.setText("4");
                break;
            default:
                break;
        }
        isTotal();
    }//GEN-LAST:event_Q10ItemStateChanged

    private void Q10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Q10KeyPressed
        Valid.pindah(evt,Q9,BtnSimpan);
    }//GEN-LAST:event_Q10KeyPressed

    private void SkalaMotivasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SkalaMotivasiKeyPressed
        //dibiarkan kosong - field ini sekarang read-only (Rekomendasi Tindak Lanjut otomatis)
    }//GEN-LAST:event_SkalaMotivasiKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMSkriningKecanduanAlkohol dialog = new RMSkriningKecanduanAlkohol(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPetugas;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Jam;
    private widget.ComboBox JamPertama;
    private widget.TextBox KdPetugas;
    private widget.TextBox Kesimpulan;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ComboBox Menit;
    private widget.ComboBox MenyalakanRokok;
    private javax.swing.JMenuItem MnSkriningKecanduanAlkohol;
    private widget.TextBox NilaiJamPertama;
    private widget.TextBox NilaiMenyalakanRokok;
    private widget.TextBox NilaiRasaIngin;
    private widget.TextBox NilaiRokokDihisab;
    private widget.TextBox NilaiSakitBerat;
    private widget.Label jLabel200;
    private widget.Label jLabel201;
    private widget.Label jLabel202;
    private widget.ComboBox Q7;
    private widget.Label jLabel203;
    private widget.TextBox NilaiQ7;
    private widget.Label jLabel204;
    private widget.Label jLabel205;
    private widget.Label jLabel206;
    private widget.ComboBox Q8;
    private widget.Label jLabel207;
    private widget.TextBox NilaiQ8;
    private widget.Label jLabel208;
    private widget.Label jLabel209;
    private widget.ComboBox Q9;
    private widget.Label jLabel210;
    private widget.TextBox NilaiQ9;
    private widget.Label jLabel211;
    private widget.Label jLabel212;
    private widget.ComboBox Q10;
    private widget.Label jLabel213;
    private widget.TextBox NilaiQ10;
    private widget.TextBox NilaiTidakRela;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ComboBox RasaIngin;
    private widget.ComboBox RokokDihisab;
    private widget.ComboBox SakitBerat;
    private widget.ScrollPane Scroll;
    private widget.TextBox SkalaMotivasi;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.ComboBox TidakRela;
    private widget.TextBox TotalNilai;
    private widget.TextBox Umur;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel101;
    private widget.Label jLabel107;
    private widget.Label jLabel108;
    private widget.Label jLabel109;
    private widget.Label jLabel110;
    private widget.Label jLabel111;
    private widget.Label jLabel112;
    private widget.Label jLabel113;
    private widget.Label jLabel114;
    private widget.Label jLabel115;
    private widget.Label jLabel116;
    private widget.Label jLabel118;
    private widget.Label jLabel148;
    private widget.Label jLabel149;
    private widget.Label jLabel150;
    private widget.Label jLabel152;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel69;
    private widget.Label jLabel7;
    private widget.Label jLabel73;
    private widget.Label jLabel75;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel8;
    private widget.Label jLabel92;
    private widget.Label jLabel95;
    private widget.Label jLabel96;
    private widget.Label jLabel97;
    private widget.Label jLabel99;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
    
    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().trim().equals("")){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,reg_periksa.umurdaftar,reg_periksa.sttsumur,skrining_kecanduan_alkohol.nip,"+
                    "petugas.nama,skrining_kecanduan_alkohol.tanggal,skrining_kecanduan_alkohol.frekuensi_minum,skrining_kecanduan_alkohol.nilai_frekuensi_minum,skrining_kecanduan_alkohol.jumlah_minum,"+
                    "skrining_kecanduan_alkohol.nilai_jumlah_minum,skrining_kecanduan_alkohol.frekuensi_minum_berlebih,skrining_kecanduan_alkohol.nilai_frekuensi_minum_berlebih,skrining_kecanduan_alkohol.tidak_bisa_berhenti,"+
                    "skrining_kecanduan_alkohol.nilai_tidak_bisa_berhenti,skrining_kecanduan_alkohol.gagal_tanggung_jawab,skrining_kecanduan_alkohol.nilai_gagal_tanggung_jawab,skrining_kecanduan_alkohol.minum_pagi_hari,"+
                    "skrining_kecanduan_alkohol.nilai_minum_pagi_hari,skrining_kecanduan_alkohol.rasa_bersalah,skrining_kecanduan_alkohol.nilai_rasa_bersalah,skrining_kecanduan_alkohol.lupa_kejadian,skrining_kecanduan_alkohol.nilai_lupa_kejadian,"+
                    "skrining_kecanduan_alkohol.cedera_akibat_minum,skrining_kecanduan_alkohol.nilai_cedera_akibat_minum,skrining_kecanduan_alkohol.saran_mengurangi,skrining_kecanduan_alkohol.nilai_saran_mengurangi,"+
                    "skrining_kecanduan_alkohol.nilai_total,skrining_kecanduan_alkohol.keterangan_hasil_skrining,skrining_kecanduan_alkohol.rekomendasi_tindak_lanjut "+
                    "from skrining_kecanduan_alkohol inner join reg_periksa on skrining_kecanduan_alkohol.no_rawat=reg_periksa.no_rawat inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on skrining_kecanduan_alkohol.nip=petugas.nip where skrining_kecanduan_alkohol.tanggal between ? and ? order by skrining_kecanduan_alkohol.tanggal ");
            }else{
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,reg_periksa.umurdaftar,reg_periksa.sttsumur,skrining_kecanduan_alkohol.nip,"+
                    "petugas.nama,skrining_kecanduan_alkohol.tanggal,skrining_kecanduan_alkohol.frekuensi_minum,skrining_kecanduan_alkohol.nilai_frekuensi_minum,skrining_kecanduan_alkohol.jumlah_minum,"+
                    "skrining_kecanduan_alkohol.nilai_jumlah_minum,skrining_kecanduan_alkohol.frekuensi_minum_berlebih,skrining_kecanduan_alkohol.nilai_frekuensi_minum_berlebih,skrining_kecanduan_alkohol.tidak_bisa_berhenti,"+
                    "skrining_kecanduan_alkohol.nilai_tidak_bisa_berhenti,skrining_kecanduan_alkohol.gagal_tanggung_jawab,skrining_kecanduan_alkohol.nilai_gagal_tanggung_jawab,skrining_kecanduan_alkohol.minum_pagi_hari,"+
                    "skrining_kecanduan_alkohol.nilai_minum_pagi_hari,skrining_kecanduan_alkohol.rasa_bersalah,skrining_kecanduan_alkohol.nilai_rasa_bersalah,skrining_kecanduan_alkohol.lupa_kejadian,skrining_kecanduan_alkohol.nilai_lupa_kejadian,"+
                    "skrining_kecanduan_alkohol.cedera_akibat_minum,skrining_kecanduan_alkohol.nilai_cedera_akibat_minum,skrining_kecanduan_alkohol.saran_mengurangi,skrining_kecanduan_alkohol.nilai_saran_mengurangi,"+
                    "skrining_kecanduan_alkohol.nilai_total,skrining_kecanduan_alkohol.keterangan_hasil_skrining,skrining_kecanduan_alkohol.rekomendasi_tindak_lanjut "+
                    "from skrining_kecanduan_alkohol inner join reg_periksa on skrining_kecanduan_alkohol.no_rawat=reg_periksa.no_rawat inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on skrining_kecanduan_alkohol.nip=petugas.nip where skrining_kecanduan_alkohol.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or "+
                    "pasien.nm_pasien like ? or skrining_kecanduan_alkohol.nip like ? or petugas.nama like ?) "+
                    "order by skrining_kecanduan_alkohol.tanggal ");
            }
                
            try {
                if(TCari.getText().trim().equals("")){
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                }else{
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                }
                    
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getDate("tgl_lahir"),rs.getString("umurdaftar")+" "+rs.getString("sttsumur"),
                        rs.getString("nip"),rs.getString("nama"),rs.getString("tanggal"),rs.getString("frekuensi_minum"),rs.getString("nilai_frekuensi_minum"),rs.getString("jumlah_minum"),
                        rs.getString("nilai_jumlah_minum"),rs.getString("frekuensi_minum_berlebih"),rs.getString("nilai_frekuensi_minum_berlebih"),rs.getString("tidak_bisa_berhenti"),rs.getString("nilai_tidak_bisa_berhenti"),
                        rs.getString("gagal_tanggung_jawab"),rs.getString("nilai_gagal_tanggung_jawab"),rs.getString("minum_pagi_hari"),rs.getString("nilai_minum_pagi_hari"),
                        rs.getString("rasa_bersalah"),rs.getString("nilai_rasa_bersalah"),rs.getString("lupa_kejadian"),rs.getString("nilai_lupa_kejadian"),
                        rs.getString("cedera_akibat_minum"),rs.getString("nilai_cedera_akibat_minum"),rs.getString("saran_mengurangi"),rs.getString("nilai_saran_mengurangi"),
                        rs.getString("nilai_total"),rs.getString("keterangan_hasil_skrining"),rs.getString("rekomendasi_tindak_lanjut")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }
    
    public void emptTeks() {
        Tanggal.setDate(new Date());
        RokokDihisab.setSelectedIndex(0);
        MenyalakanRokok.setSelectedIndex(0);
        TidakRela.setSelectedIndex(0);
        JamPertama.setSelectedIndex(0);
        RasaIngin.setSelectedIndex(0);
        SakitBerat.setSelectedIndex(0);
        Q7.setSelectedIndex(0);
        Q8.setSelectedIndex(0);
        Q9.setSelectedIndex(0);
        Q10.setSelectedIndex(0);
        SkalaMotivasi.setText("Risiko Rendah - Edukasi Alkohol");
        RokokDihisab.requestFocus();
    } 

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Umur.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString().substring(11,13));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString().substring(14,15));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString().substring(17,19));
            RokokDihisab.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            NilaiRokokDihisab.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            MenyalakanRokok.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            NilaiMenyalakanRokok.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            TidakRela.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            NilaiTidakRela.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            JamPertama.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            NilaiJamPertama.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            RasaIngin.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            NilaiRasaIngin.setText(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            SakitBerat.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            NilaiSakitBerat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
            Q7.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),20).toString());
            NilaiQ7.setText(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString());
            Q8.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),22).toString());
            NilaiQ8.setText(tbObat.getValueAt(tbObat.getSelectedRow(),23).toString());
            Q9.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),24).toString());
            NilaiQ9.setText(tbObat.getValueAt(tbObat.getSelectedRow(),25).toString());
            Q10.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),26).toString());
            NilaiQ10.setText(tbObat.getValueAt(tbObat.getSelectedRow(),27).toString());
            TotalNilai.setText(tbObat.getValueAt(tbObat.getSelectedRow(),28).toString());
            Kesimpulan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),29).toString());
            SkalaMotivasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),30).toString());
            Valid.SetTgl(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());  
        }
    }
    
    private void isRawat() {
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,"+
                    "reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.umurdaftar,reg_periksa.sttsumur "+
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi")+" "+rs.getString("jam_reg"));
                    Umur.setText(rs.getString("umurdaftar")+" "+rs.getString("sttsumur"));
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
    }
 
    public void setNoRm(String norwt,Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);    
        isRawat(); 
        ChkInput.setSelected(true);
        isForm();
        runBackground(() ->tampil());
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            if(internalFrame1.getHeight()>573){
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH,375));
                FormInput.setVisible(true);      
                ChkInput.setVisible(true);
            }else{
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH,internalFrame1.getHeight()-175));
                FormInput.setVisible(true);      
                ChkInput.setVisible(true);
            }
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getskrining_kecanduan_alkohol());
        BtnHapus.setEnabled(akses.getskrining_kecanduan_alkohol());
        BtnEdit.setEnabled(akses.getskrining_kecanduan_alkohol());
        BtnPrint.setEnabled(akses.getskrining_kecanduan_alkohol()); 
        if(akses.getjml2()>=1){
            KdPetugas.setEditable(false);
            BtnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(Sequel.CariPetugas(KdPetugas.getText()));
            if(NmPetugas.getText().equals("")){
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan petugas...!!");
            }
        }  
        
        if(TANGGALMUNDUR.equals("no")){
            if(!akses.getkode().equals("Admin Utama")){
                Tanggal.setEditable(false);
                Tanggal.setEnabled(false);
                ChkKejadian.setEnabled(false);
                Jam.setEnabled(false);
                Menit.setEnabled(false);
                Detik.setEnabled(false);
            }
        }
    }

    private void jam(){
        ActionListener taskPerformer = new ActionListener(){
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                
                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if(ChkKejadian.isSelected()==true){
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                }else if(ChkKejadian.isSelected()==false){
                    nilai_jam =Jam.getSelectedIndex();
                    nilai_menit =Menit.getSelectedIndex();
                    nilai_detik =Detik.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                Jam.setSelectedItem(jam);
                Menit.setSelectedItem(menit);
                Detik.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }

    private void ganti() {
        if(Sequel.mengedittf("skrining_kecanduan_alkohol","no_rawat=?","no_rawat=?,tanggal=?,frekuensi_minum=?,nilai_frekuensi_minum=?,jumlah_minum=?,nilai_jumlah_minum=?,frekuensi_minum_berlebih=?,nilai_frekuensi_minum_berlebih=?,tidak_bisa_berhenti=?,nilai_tidak_bisa_berhenti=?,gagal_tanggung_jawab=?,nilai_gagal_tanggung_jawab=?,minum_pagi_hari=?,nilai_minum_pagi_hari=?,"+
                "rasa_bersalah=?,nilai_rasa_bersalah=?,lupa_kejadian=?,nilai_lupa_kejadian=?,cedera_akibat_minum=?,nilai_cedera_akibat_minum=?,saran_mengurangi=?,nilai_saran_mengurangi=?,"+
                "nilai_total=?,keterangan_hasil_skrining=?,rekomendasi_tindak_lanjut=?,nip=?",27,new String[]{
                TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),RokokDihisab.getSelectedItem().toString(),NilaiRokokDihisab.getText(),MenyalakanRokok.getSelectedItem().toString(),NilaiMenyalakanRokok.getText(),
                TidakRela.getSelectedItem().toString(),NilaiTidakRela.getText(),JamPertama.getSelectedItem().toString(),NilaiJamPertama.getText(),RasaIngin.getSelectedItem().toString(),NilaiRasaIngin.getText(),SakitBerat.getSelectedItem().toString(),NilaiSakitBerat.getText(),
                Q7.getSelectedItem().toString(),NilaiQ7.getText(),Q8.getSelectedItem().toString(),NilaiQ8.getText(),Q9.getSelectedItem().toString(),NilaiQ9.getText(),Q10.getSelectedItem().toString(),NilaiQ10.getText(),
                TotalNilai.getText(),Kesimpulan.getText(),
                SkalaMotivasi.getText(),KdPetugas.getText(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
            })==true){
               tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
               tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
               tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
               tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
               tbObat.setValueAt(Umur.getText(),tbObat.getSelectedRow(),4);
               tbObat.setValueAt(KdPetugas.getText(),tbObat.getSelectedRow(),5);
               tbObat.setValueAt(NmPetugas.getText(),tbObat.getSelectedRow(),6);
               tbObat.setValueAt(Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),tbObat.getSelectedRow(),7);
               tbObat.setValueAt(RokokDihisab.getSelectedItem().toString(),tbObat.getSelectedRow(),8);
               tbObat.setValueAt(NilaiRokokDihisab.getText(),tbObat.getSelectedRow(),9);
               tbObat.setValueAt(MenyalakanRokok.getSelectedItem().toString(),tbObat.getSelectedRow(),10);
               tbObat.setValueAt(NilaiMenyalakanRokok.getText(),tbObat.getSelectedRow(),11);
               tbObat.setValueAt(TidakRela.getSelectedItem().toString(),tbObat.getSelectedRow(),12);
               tbObat.setValueAt(NilaiTidakRela.getText(),tbObat.getSelectedRow(),13);
               tbObat.setValueAt(JamPertama.getSelectedItem().toString(),tbObat.getSelectedRow(),14);
               tbObat.setValueAt(NilaiJamPertama.getText(),tbObat.getSelectedRow(),15);
               tbObat.setValueAt(RasaIngin.getSelectedItem().toString(),tbObat.getSelectedRow(),16);
               tbObat.setValueAt(NilaiRasaIngin.getText(),tbObat.getSelectedRow(),17);
               tbObat.setValueAt(SakitBerat.getSelectedItem().toString(),tbObat.getSelectedRow(),18);
               tbObat.setValueAt(NilaiSakitBerat.getText(),tbObat.getSelectedRow(),19);
               tbObat.setValueAt(Q7.getSelectedItem().toString(),tbObat.getSelectedRow(),20);
               tbObat.setValueAt(NilaiQ7.getText(),tbObat.getSelectedRow(),21);
               tbObat.setValueAt(Q8.getSelectedItem().toString(),tbObat.getSelectedRow(),22);
               tbObat.setValueAt(NilaiQ8.getText(),tbObat.getSelectedRow(),23);
               tbObat.setValueAt(Q9.getSelectedItem().toString(),tbObat.getSelectedRow(),24);
               tbObat.setValueAt(NilaiQ9.getText(),tbObat.getSelectedRow(),25);
               tbObat.setValueAt(Q10.getSelectedItem().toString(),tbObat.getSelectedRow(),26);
               tbObat.setValueAt(NilaiQ10.getText(),tbObat.getSelectedRow(),27);
               tbObat.setValueAt(TotalNilai.getText(),tbObat.getSelectedRow(),28);
               tbObat.setValueAt(Kesimpulan.getText(),tbObat.getSelectedRow(),29);
               tbObat.setValueAt(SkalaMotivasi.getText(),tbObat.getSelectedRow(),30);
               emptTeks();
        }
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from skrining_kecanduan_alkohol where no_rawat=?",1,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(""+tabMode.getRowCount());
            emptTeks();
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }

    private void isTotal() {
        try {
            TotalNilai.setText(""+(
                    Integer.parseInt(NilaiRokokDihisab.getText())+Integer.parseInt(NilaiMenyalakanRokok.getText())+
                    Integer.parseInt(NilaiTidakRela.getText())+
                    Integer.parseInt(NilaiJamPertama.getText())+
                    Integer.parseInt(NilaiRasaIngin.getText())+Integer.parseInt(NilaiSakitBerat.getText())+
                    Integer.parseInt(NilaiQ7.getText())+Integer.parseInt(NilaiQ8.getText())+
                    Integer.parseInt(NilaiQ9.getText())+Integer.parseInt(NilaiQ10.getText())
            ));
            int total = Integer.parseInt(TotalNilai.getText());
            if(total>=20){
                Kesimpulan.setText("Kemungkinan Ketergantungan Alkohol (Zona IV)");
                SkalaMotivasi.setText("Rujukan ke spesialis untuk evaluasi diagnostik dan tatalaksana lebih lanjut");
            }else if(total>=16){
                Kesimpulan.setText("Berbahaya / Merugikan (Zona III)");
                SkalaMotivasi.setText("Saran sederhana ditambah konseling singkat dan pemantauan berkelanjutan");
            }else if(total>=8){
                Kesimpulan.setText("Berisiko / Hazardous (Zona II)");
                SkalaMotivasi.setText("Saran sederhana (Simple Advice) untuk mengurangi konsumsi alkohol");
            }else{
                Kesimpulan.setText("Risiko Rendah (Zona I)");
                SkalaMotivasi.setText("Edukasi Alkohol - berikan informasi umum mengenai risiko konsumsi alkohol");
            }
        } catch (Exception e) {
            Kesimpulan.setText("Risiko Rendah (Zona I)");
            SkalaMotivasi.setText("Edukasi Alkohol - berikan informasi umum mengenai risiko konsumsi alkohol");
        }
    }
      

    private void simpan() {
        if(Sequel.menyimpantf("skrining_kecanduan_alkohol","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",26,new String[]{
            TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),RokokDihisab.getSelectedItem().toString(),NilaiRokokDihisab.getText(),
            MenyalakanRokok.getSelectedItem().toString(),NilaiMenyalakanRokok.getText(),TidakRela.getSelectedItem().toString(),NilaiTidakRela.getText(),JamPertama.getSelectedItem().toString(),NilaiJamPertama.getText(),
            RasaIngin.getSelectedItem().toString(),NilaiRasaIngin.getText(),SakitBerat.getSelectedItem().toString(),NilaiSakitBerat.getText(),
            Q7.getSelectedItem().toString(),NilaiQ7.getText(),Q8.getSelectedItem().toString(),NilaiQ8.getText(),
            Q9.getSelectedItem().toString(),NilaiQ9.getText(),Q10.getSelectedItem().toString(),NilaiQ10.getText(),
            TotalNilai.getText(),Kesimpulan.getText(),SkalaMotivasi.getText(),
            KdPetugas.getText()
        })==true){
            tabMode.addRow(new Object[]{
                TNoRw.getText(),TNoRM.getText(),TPasien.getText(),TglLahir.getText(),Umur.getText(),KdPetugas.getText(),NmPetugas.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
                RokokDihisab.getSelectedItem().toString(),NilaiRokokDihisab.getText(),MenyalakanRokok.getSelectedItem().toString(),NilaiMenyalakanRokok.getText(),TidakRela.getSelectedItem().toString(),NilaiTidakRela.getText(),JamPertama.getSelectedItem().toString(),
                NilaiJamPertama.getText(),RasaIngin.getSelectedItem().toString(),NilaiRasaIngin.getText(),SakitBerat.getSelectedItem().toString(),NilaiSakitBerat.getText(),
                Q7.getSelectedItem().toString(),NilaiQ7.getText(),Q8.getSelectedItem().toString(),NilaiQ8.getText(),
                Q9.getSelectedItem().toString(),NilaiQ9.getText(),Q10.getSelectedItem().toString(),NilaiQ10.getText(),
                TotalNilai.getText(),Kesimpulan.getText(),SkalaMotivasi.getText()
            });
            LCount.setText(""+tabMode.getRowCount());
            emptTeks();
        } 
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }
    
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
