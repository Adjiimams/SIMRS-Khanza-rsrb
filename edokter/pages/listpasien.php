<div class="block-header">
    <h2><center>DAFTAR PASIEN HARI INI</center></h2>
</div>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        <div class="card">
            <div class="body">
                <div class="table-responsive">
                    <table class="table table-bordered table-striped table-hover js-basic-example dataTable">
                        <thead>
                            <tr>
                                <th width="5%"><center>No.Poli</center></th>
                                <th width="15%"><center>No.Rawat</center></th>
                                <th width="10%"><center>No.RM</center></th>
                                <th width="48%"><center>Nama Pasien</center></th>
                                <th width="5%"><center>JK</center></th>
                                <th width="5%"><center>Umur</center></th>
                                <th width="7%"><center>Status</center></th>
                                <th width="5%"><center>PRB</center></th>
                            </tr>
                        </thead>
                        <tbody>
                        <?php 
                           $querypasien = bukaquery(
                               "SELECT reg_periksa.no_reg, reg_periksa.no_rawat, reg_periksa.no_rkm_medis,
                                       pasien.nm_pasien, pasien.jk,
                                       concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,
                                       reg_periksa.stts
                                FROM reg_periksa
                                INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis
                                WHERE reg_periksa.kd_dokter='".validTeks4(encrypt_decrypt($_SESSION["ses_dokter"],"d"),20)."'
                                AND reg_periksa.tgl_registrasi = current_date()"
                           );
                           while($rsquerypasien = mysqli_fetch_array($querypasien)) {
                               $no_rawat = $rsquerypasien["no_rawat"];

                               // Cek PRB via bridging_sep → bpjs_prb
                               $dataPRB = mysqli_fetch_array(bukaquery(
                                   "SELECT bpjs_prb.prb, bridging_sep.no_sep, pasien.nm_pasien,
                                           pasien.no_rkm_medis, reg_periksa.no_rawat,
                                           date_format(reg_periksa.tgl_registrasi,'%d-%m-%Y') as tgl_kunjungan,
                                           dokter.nm_dokter
                                    FROM bpjs_prb
                                    INNER JOIN bridging_sep ON bpjs_prb.no_sep = bridging_sep.no_sep
                                    INNER JOIN reg_periksa ON bridging_sep.no_rawat = reg_periksa.no_rawat
                                    INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis
                                    INNER JOIN dokter ON reg_periksa.kd_dokter = dokter.kd_dokter
                                    WHERE bridging_sep.no_rawat = '$no_rawat'
                                    LIMIT 1"
                               ));
                               $isPRB = !empty($dataPRB);

                               // Badge PRB di kolom — klik untuk buka modal
                               $badgePRB = $isPRB
                                   ? "<a href='javascript:void(0);'
                                         onclick='tampilModalPRB(".json_encode($dataPRB).")'
                                         title='Pasien PRB - Klik untuk detail'>
                                         <span style='background:#c0392b;color:#fff;padding:3px 8px;border-radius:10px;font-size:11px;font-weight:bold;cursor:pointer;'>
                                             &#9888; PRB
                                         </span>
                                      </a>"
                                   : "<span style='color:#aaa;font-size:11px;'>-</span>";

                               echo "<tr>
                                       <td align='left'>".$rsquerypasien["no_reg"]."</td>
                                       <td align='left'>".$rsquerypasien["no_rawat"]."</td>
                                       <td align='left'>".$rsquerypasien["no_rkm_medis"]."</td>
                                       <td align='left'>".$rsquerypasien["nm_pasien"]."</td>
                                       <td align='left'>".$rsquerypasien["jk"]."</td>
                                       <td align='left'>".$rsquerypasien["umur"]."</td>
                                       <td align='left'>".$rsquerypasien["stts"]."</td>
                                       <td align='center'>".$badgePRB."</td>
                                     </tr>";
                           }
                        ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- =====================================================
     MODAL PRB
     ===================================================== -->
<div id="modalPRB" style="display:none; position:fixed; z-index:9999; top:0; left:0;
     width:100%; height:100%; background:rgba(0,0,0,0.6); justify-content:center; align-items:center;">
    <div style="background:#fff; border-radius:6px; width:520px; max-width:95%; box-shadow:0 8px 32px rgba(0,0,0,0.3); overflow:hidden;">

        <!-- Header merah -->
        <div style="background:#c0392b; color:#fff; padding:14px 20px; display:flex; align-items:center; justify-content:space-between;">
            <span style="font-size:16px; font-weight:bold;">
                <i class="material-icons" style="vertical-align:middle; font-size:20px;">warning</i>
                &nbsp;INFORMASI PASIEN PRB
            </span>
            <span onclick="tutupModalPRB()" style="cursor:pointer; font-size:22px; line-height:1;">&times;</span>
        </div>

        <!-- Subheader -->
        <div style="background:#e74c3c; color:#fff; padding:6px 20px; font-size:12px;">
            Pasien ini terdaftar dalam Program Rujuk Balik (PRB) BPJS Kesehatan.
            Pertimbangkan kelanjutan terapi di FKTP sesuai ketentuan.
        </div>

        <!-- Body detail -->
        <div style="padding:20px;">
            <table width="100%" style="font-size:13px; border-collapse:collapse;">
                <tr>
                    <td width="38%" style="padding:6px 4px; color:#555;">No. Rawat</td>
                    <td style="padding:6px 4px; font-weight:bold;" id="prb_no_rawat">-</td>
                </tr>
                <tr style="background:#f9f9f9;">
                    <td style="padding:6px 4px; color:#555;">No. RM</td>
                    <td style="padding:6px 4px; font-weight:bold;" id="prb_no_rm">-</td>
                </tr>
                <tr>
                    <td style="padding:6px 4px; color:#555;">Nama Pasien</td>
                    <td style="padding:6px 4px; font-weight:bold;" id="prb_nm_pasien">-</td>
                </tr>
                <tr style="background:#f9f9f9;">
                    <td style="padding:6px 4px; color:#555;">No. SEP</td>
                    <td style="padding:6px 4px;" id="prb_no_sep">-</td>
                </tr>
                <tr>
                    <td style="padding:6px 4px; color:#555;">Kode PRB</td>
                    <td style="padding:6px 4px;">
                        <span id="prb_kode" style="background:#c0392b;color:#fff;padding:2px 10px;border-radius:10px;font-weight:bold;"></span>
                    </td>
                </tr>
                <tr style="background:#f9f9f9;">
                    <td style="padding:6px 4px; color:#555;">Dokter</td>
                    <td style="padding:6px 4px;" id="prb_dokter">-</td>
                </tr>
                <tr>
                    <td style="padding:6px 4px; color:#555;">Tanggal Kunjungan</td>
                    <td style="padding:6px 4px;" id="prb_tgl">-</td>
                </tr>
            </table>
        </div>

        <!-- Tombol aksi -->
        <div style="padding:12px 20px 18px 20px; text-align:center; border-top:1px solid #eee;">
            <button onclick="cetakSuratPRB()"
                class="btn btn-danger waves-effect"
                style="margin-right:10px;">
                <i class="material-icons" style="vertical-align:middle;font-size:16px;">print</i>
                &nbsp;Cetak Surat PRB
            </button>
            <button onclick="tutupModalPRB()" class="btn btn-default waves-effect">
                Tutup
            </button>
        </div>
    </div>
</div>

<!-- =====================================================
     IFRAME TERSEMBUNYI untuk cetak surat PRB
     ===================================================== -->
<iframe id="frameCetakPRB" style="display:none;"></iframe>

<script>
var _dataPRB = {};

function tampilModalPRB(data) {
    _dataPRB = data;
    document.getElementById('prb_no_rawat').innerText  = data.no_rawat   || '-';
    document.getElementById('prb_no_rm').innerText     = data.no_rkm_medis || '-';
    document.getElementById('prb_nm_pasien').innerText = data.nm_pasien  || '-';
    document.getElementById('prb_no_sep').innerText    = data.no_sep     || '-';
    document.getElementById('prb_kode').innerText      = data.prb        || '-';
    document.getElementById('prb_dokter').innerText    = data.nm_dokter  || '-';
    document.getElementById('prb_tgl').innerText       = data.tgl_kunjungan || '-';
    document.getElementById('modalPRB').style.display  = 'flex';
}

function tutupModalPRB() {
    document.getElementById('modalPRB').style.display = 'none';
}

function cetakSuratPRB() {
    if (!_dataPRB.no_rawat) return;
    var url = 'index.php?act=CetakPRB&iyem=' + encodeURIComponent('<?php echo encrypt_decrypt(\'__NORAWAT__\',\'e\'); ?>'.replace('__NORAWAT__', _dataPRB.no_rawat));
    // Buka di tab baru untuk cetak
    window.open(url, '_blank', 'width=800,height=600');
}

// Tutup modal jika klik background
document.getElementById('modalPRB').addEventListener('click', function(e){
    if (e.target === this) tutupModalPRB();
});
</script>
