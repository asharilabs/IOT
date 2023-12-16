<?php

include('koneksi.php');

if( !$con){
    echo 'koneksi gagal';
    return;
}

$query = "SELECT ROUND(MIN(suhu),2) AS suhu_max, 
	ROUND(MAX(suhu),2) AS suhu_min, 
    ROUND(AVG(suhu), 2) AS suhu_rata, 
    ROUND(MIN(humid),2) AS humid_max, 
    ROUND(MAX(humid),2) AS humid_min, 
    ROUND(AVG(humid),2) AS humid_rata FROM tb_dht";
$result = mysqli_query($con, $query);

if( mysqli_num_rows($result) > 0){
    while($rows = mysqli_fetch_assoc($result)){
        $arr = array(
            "suhu_min" => $rows['suhu_min'],
            "suhu_max" => $rows['suhu_max'],
            "suhu_rata" => $rows['suhu_rata'],
            "humid_min" => $rows['humid_min'],
            "humid_max" => $rows['humid_max'],
            "humid_rata" => $rows['humid_rata'],
        );
    }
    
    echo json_encode($arr);
}
else{
    echo 'tidak ada data';
}

?>