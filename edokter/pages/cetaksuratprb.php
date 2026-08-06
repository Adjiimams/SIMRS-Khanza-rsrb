<?php
    if(strpos($_SERVER['REQUEST_URI'],"pages")){
        exit(header("Location:../index.php"));
    }

    $iyem     = trim(isset($_GET['iyem'])) ? trim($_GET['iyem']) : NULL;
    $no_rawat = validTeks3(encrypt_decrypt($iyem, "d"), 20);

    if (empty($no_rawat)) {
        echo "<center>Data tidak ditemukan.</center>"; exit;
    }

    // Ambil data lengkap untuk surat PRB
    $queryPRB = bukaquery(
        "SELECT bpjs_prb.prb, bpjs_prb.no_sep,
                pasien.nm_pasien, pasien.no_rkm_medis, pasien.jk,
                concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,
                reg_periksa.no_rawat, reg_periksa.tgl_registrasi,
                dokter.nm_dokter, poli.nm_poli
         FROM bpjs_prb
         INNER JOIN bridging_sep   ON bpjs_prb.no_sep         = bridging_sep.no_sep
         INNER JOIN reg_periksa    ON bridging_sep.no_rawat    = reg_periksa.no_rawat
         INNER JOIN pasien         ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis
         INNER JOIN dokter         ON reg_periksa.kd_dokter    = dokter.kd_dokter
         INNER JOIN poliklinik as poli ON reg_periksa.kd_poli  = poli.kd_poli
         WHERE bridging_sep.no_rawat = '$no_rawat'
         LIMIT 1"
    );

    if (!($rs = mysqli_fetch_array($queryPRB))) {
        echo "<center>Data PRB tidak ditemukan untuk No.Rawat: $no_rawat</center>"; exit;
    }
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Surat PRB - <?= htmlspecialchars($rs["nm_pasien"]) ?></title>
    <style>
        * { margin:0; padding:0; box-sizing:border-box; }
        body { font-family: Arial, sans-serif; font-size:12px; color:#000; background:#fff; padding:20px; }

        .kop { text-align:center; border-bottom:3px double #000; padding-bottom:10px; margin-bottom:14px; }
        .kop h2 { font-size:15px; text-transform:uppercase; }
        .kop h3 { font-size:13px; font-weight:normal; }
        .kop p  { font-size:11px; }

        .judul-surat { text-align:center; margin:14px 0 4px; }
        .judul-surat h3 { font-size:14px; text-decoration:underline; text-transform:uppercase; letter-spacing:1px; }

        .badge-prb {
            display:inline-block;
            background:#c0392b; color:#fff;
            padding:4px 16px; border-radius:12px;
            font-size:12px; font-weight:bold;
            margin:6px auto; letter-spacing:1px;
        }

        table.data-pasien { width:100%; margin:12px 0; border-collapse:collapse; }
        table.data-pasien td { padding:5px 8px; font-size:12px; }
        table.data-pasien td:first-child { width:38%; color:#333; }

        .box-info {
            border:1px solid #c0392b;
            background:#fff5f5;
            border-radius:4px;
            padding:10px 14px;
            margin:14px 0;
            font-size:12px;
            line-height:1.6;
        }

        .ttd { margin-top:30px; }
        .ttd table { width:100%; }
        .ttd td { text-align:center; padding:6px; font-size:12px; }
        .ttd .garis { margin-top:50px; border-top:1px solid #000; width:180px; margin-left:auto; margin-right:auto; }

        @media print {
            body { padding:10px; }
            .no-print { display:none !important; }
            @page { size:A5; margin:1cm; }
        }
    </style>
</head>
<body>

<!-- Tombol cetak (hilang saat print) -->
<div class="no-print" style="margin-bottom:14px; text-align:right;">
    <button onclick="window.print()"
        style="background:#c0392b;color:#fff;border:none;padding:8px 20px;border-radius:4px;cursor:pointer;font-size:13px;">
        &#128438; Cetak Surat
    </button>
    <button onclick="window.close()"
        style="background:#777;color:#fff;border:none;padding:8px 16px;border-radius:4px;cursor:pointer;font-size:13px;margin-left:8px;">
        Tutup
    </button>
</div>

<!-- KOP SURAT -->
<div class="kop">
    <h2><?= htmlspecialchars($_SESSION["nama_instansi"]) ?></h2>
    <h3>Surat Keterangan Program Rujuk Balik (PRB)</h3>
    <p>Dicetak pada: <?= date("d-m-Y H:i:s") ?></p>
</div>

<!-- Judul -->
<div class="judul-surat">
    <h3>Surat Keterangan PRB</h3>
    <div>
        <span class="badge-prb">KODE PRB: <?= htmlspecialchars($rs["prb"]) ?></span>
    </div>
</div>

<!-- Data Pasien -->
<table class="data-pasien">
    <tr>
        <td>No. Rawat</td>
        <td>: <strong><?= htmlspecialchars($rs["no_rawat"]) ?></strong></td>
    </tr>
    <tr>
        <td>No. RM</td>
        <td>: <?= htmlspecialchars($rs["no_rkm_medis"]) ?></td>
    </tr>
    <tr>
        <td>Nama Pasien</td>
        <td>: <strong><?= htmlspecialchars($rs["nm_pasien"]) ?></strong></td>
    </tr>
    <tr>
        <td>Jenis Kelamin</td>
        <td>: <?= ($rs["jk"]=="L") ? "Laki-laki" : "Perempuan" ?></td>
    </tr>
    <tr>
        <td>Umur</td>
        <td>: <?= htmlspecialchars($rs["umur"]) ?></td>
    </tr>
    <tr>
        <td>No. SEP</td>
        <td>: <?= htmlspecialchars($rs["no_sep"]) ?></td>
    </tr>
    <tr>
        <td>Poli / Layanan</td>
        <td>: <?= htmlspecialchars($rs["nm_poli"]) ?></td>
    </tr>
    <tr>
        <td>Tanggal Kunjungan</td>
        <td>: <?= date("d-m-Y", strtotime($rs["tgl_registrasi"])) ?></td>
    </tr>
    <tr>
        <td>Dokter Pemeriksa</td>
        <td>: <?= htmlspecialchars($rs["nm_dokter"]) ?></td>
    </tr>
</table>

<!-- Kotak informasi -->
<div class="box-info">
    <strong>&#9888; Keterangan:</strong><br>
    Pasien di atas terdaftar dalam <strong>Program Rujuk Balik (PRB)</strong> BPJS Kesehatan dengan kode
    <strong><?= htmlspecialchars($rs["prb"]) ?></strong>.<br>
    Pasien disarankan untuk melanjutkan pengobatan rutin di <strong>Fasilitas Kesehatan Tingkat Pertama (FKTP)</strong>
    sesuai dengan ketentuan BPJS Kesehatan yang berlaku.<br><br>
    Surat ini diterbitkan sebagai bukti bahwa pasien merupakan peserta PRB aktif.
</div>

<!-- Tanda Tangan -->
<div class="ttd">
    <table>
        <tr>
            <td></td>
            <td>
                <?= htmlspecialchars($_SESSION["nama_instansi"]) ?>,
                <?= date("d-m-Y") ?><br><br>
                Dokter Pemeriksa,
                <div class="garis"></div>
                <strong><?= htmlspecialchars($rs["nm_dokter"]) ?></strong>
            </td>
        </tr>
    </table>
</div>

</body>
</html>
