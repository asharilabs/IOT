<?php

include('koneksi.php');

if( !$con){
    echo 'koneksi gagal';
    return;
}

// input data
$input_suhu = $_GET['suhu'];
$input_humid = $_GET['humid'];

// time configuration
date_default_timezone_set('Asia/Jakarta');
$waktu = date('Y-m-d H:i:s');

$query = "INSERT INTO tb_dht(suhu, humid, ts) VALUES ( $input_suhu, $input_humid, '$waktu')";
$result = mysqli_query($con, $query);

if( $result){
    echo 'berhasil input data';
}
else{
    echo 'gagal input data';
}

?>