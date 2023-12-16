<?php

include('koneksi.php');

if( !$con){
    echo 'koneksi gagal';
    return;
}

$banyakhari = $_GET['hari'];

$query = "SELECT 
	ROUND(AVG(ratasuhu), 2) AS rataratasuhu,
    ROUND(AVG(ratahumid), 2) AS rataratahumid
FROM
	(SELECT ROUND(AVG(suhu),2) AS ratasuhu, ROUND(AVG(humid),2) AS ratahumid, COUNT(*) AS banyakdata 
     FROM tb_dht GROUP BY date(ts) ORDER BY ts DESC LIMIT 0,$banyakhari) A";
$result = mysqli_query($con, $query);

if( mysqli_num_rows($result) > 0){
    
    while($rows = mysqli_fetch_assoc($result)){
        $arr = array(
            "ratasuhu" => $rows['rataratasuhu'],
            "ratahumid" => $rows['rataratahumid']
        );
    }
    
    echo json_encode($arr);
}
else{
    echo 'tidak ada data';
}

?>