<?php

include('koneksi.php');

if( !$con){
    echo 'koneksi gagal';
    return;
}

$query = "SELECT suhu FROM `tb_dht` GROUP BY suhu ORDER BY suhu DESC LIMIT 0,3";
$result = mysqli_query($con, $query);

$finaldata = array();

if( mysqli_num_rows($result) > 0){
    // data ada
    while($rows = mysqli_fetch_assoc($result)){
        $objekdata = "";
        
        $nilaimax = $rows['suhu'];
        // echo 'nilai max: ' . $nilaimax . "<br>";
        
        // query yang kedua
        $query2 = "SELECT ts FROM tb_dht WHERE suhu = $nilaimax";
        $result2 = mysqli_query($con, $query2);
        
        $arrTgl = array();
        
        if( mysqli_num_rows($result2) > 0){
            
            while($rows2 = mysqli_fetch_assoc($result2)){
                array_push($arrTgl, $rows2['ts']);
            }
        }
        else{
            echo 'tidak ada data di query2';
        }
        
        // bangun objek data
        $objekdata = array("nilai" => $nilaimax, "tgl" => $arrTgl);
        array_push($finaldata, $objekdata);
    }
    
    echo json_encode(array("suhu" => $finaldata));
}
else{
    echo 'tidak ada data';
}

?>