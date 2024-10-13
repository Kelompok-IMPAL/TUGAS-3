package com.mycompany.transferpulsa;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class NomorTelepon {
    String nomor;
    int pulsa;
    
    NomorTelepon(String nomor, int pulsa) {
        this.nomor = nomor;
        this.pulsa = pulsa;
    }
    
    void terimaSMS(String pesan) {
        System.out.println("Pesan diterima pada nomor " + this.nomor + " : " + pesan);
    }
}

public class TransferPulsa {
    
    //Convert nomor berawalan 628 ke 08
    static String convertFormat(String nomorTelepon) {
        if (nomorTelepon.startsWith("628")) {
            return "08" + nomorTelepon.substring(3);
        }
        
        return nomorTelepon;
    }
    
    // Cari nomor di database nomor aktif
    static boolean nomorAktif(String nomorTelepon) {
        // Contoh database nomor aktif
        List<String> nomorAktif = Arrays.asList(
        "081112345678",
        "08121234567",
        "0813123456789",
        "082112345678",
        "0822123456789",
        "085312345678",
        "081293607503"
    );
    
    //Jika nomor ada di list nomor aktif
    if (nomorAktif.contains(nomorTelepon)) {
        return true;
    }
    
    return false;
 }
    
    //Cek jika nomor penerima dapat di transfer pulsa
    static boolean nomorValid(String nomorTelepon) {
        // Jika nomor telepon tidak ada / kosong
        if (nomorTelepon == null || nomorTelepon.isEmpty()) {
            return false;
        }
        
        // Jika nomor telepon tidak diawali 08 atau 628
        if (!(nomorTelepon.startsWith("08") || nomorTelepon.startsWith("628"))) {
            return false;
        }
        
        // Jika nomor telepon tidak kurang dari 11 digit atau lebih dari 13 digit
        if (nomorTelepon.length() < 11 || nomorTelepon.length() > 13) {
            return false;
        }
        
        // Digit ketiga dan keempat sebagai kode nomor telkomsel
        List<String> kode = Arrays.asList("11", "12", "13", "21", "22", "23", "51", "52", "53");
        
        // Jika digit ketiga dan keempat nomor telepon tidak ada di list kode valid
        if (!kode.contains(nomorTelepon.substring(2, 4))) {
            return false;
        }
        
        // Jika nomor telepon tidak aktif
        if (!nomorAktif(nomorTelepon)) {
            return false;
        }
        
        return true;
        
    }
   
    static void transferPulsa(NomorTelepon pengirim, NomorTelepon penerima, int jumlah, int tarif) {
        // Pulsa pengirim berkuran sebanyak jumlah dan tarif, pulsa penerima bertambah sebanyak jumlah
        pengirim.pulsa -= (jumlah + tarif);
        penerima.pulsa += jumlah;
    }
    
    static void kirimSMS(NomorTelepon nomor, String pesan) {
        nomor.terimaSMS(pesan);
    }
    
    // Simulasi trasnfer pulsa
    // Asumsi sudah masuk ke menu 1, bukan dari menu utama
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        final int tarif = 1850;
        
        //Pengirim
        NomorTelepon nomor1 = new NomorTelepon("085249324884", 100000);
        
        //Penerima
        NomorTelepon nomor2 = new NomorTelepon("081293607503", 0);
        
        // Contoh database nomor telepon
        NomorTelepon[] listNomor = new NomorTelepon[10];
        listNomor[0] = nomor1;
        listNomor[1] = nomor2;
        
        System.out.println("Silahkan masukkan nomor tujuan Transfer Pulsa : (contoh: 08xxxx atau 628xxxx)");
        String nomorPenerima = scanner.nextLine();
        nomorPenerima = convertFormat(nomorPenerima);
        
        // Jika nomor telepon valid 
        if (nomorValid(nomorPenerima)) {
            // Pencarian nomor penerima di database
            for (NomorTelepon nomorTelepon : listNomor) {
                // Nomor telepon ditemukan
                if (nomorTelepon.nomor.equals(nomorPenerima)) {
                    System.out.println("Silahkan masukkan jumlah pulsa yang akan ditransfer : (min 5000, max 1 jt & tanpa . (titik) atau , (koma)");  
                    int jumlah = scanner.nextInt();
                    scanner.nextLine();
                    
                    // Pulsa yang akan dikirim kurang dari 5000 atau lebih dari 1 juta
                    if (jumlah < 5000 || jumlah > 1000000) {
                        System.out.println("Jumlah pulsa yang ditransfer tidak valid");
                        return;
                    }
                    
                    // Pulsa yang akan dikirim melebihi pulsa yang dimiliki saat ini
                    if (jumlah + tarif > nomor1.pulsa) {
                        System.out.println("Jumlah pulsa yang dimiliki saat ini tidak cukup untuk melakukan transfer");
                        return;
                    }
                    
                    System.out.println("Anda akan transfer pulsa " + jumlah + " ke nomor " + nomorTelepon.nomor + " ? (Biaya : " + tarif + ", Balas 1 jika Ya)");
                    
                    int konfirmasi = scanner.nextInt();
                    scanner.nextLine();
                       
                    // Konfirmasi pengiriman pulsa
                    if (konfirmasi == 1) {
                        // Semua validasi berhasil
                        System.out.println("Terima kasih permintaan anda sedang diproses.");
                        transferPulsa(nomor1, nomorTelepon, jumlah, tarif);
                        
                        kirimSMS(nomor1, "Transfer pulsa sebesar Rp." + jumlah + " ke nomor " + nomorTelepon.nomor + " berhasil. Sisa pulsa anda sekarang adalah " + nomor1.pulsa);
                        kirimSMS(nomor2, "Anda mendapatkan penambahan pulsa Rp. " + jumlah + "dari nomor " + nomor1.nomor + ". Sisa pulsa anda sekarang adalah " + nomorTelepon.pulsa);
                        return;
                    } else {
                        return;
                    }
                } 
            }
            
            System.out.println("Nomor telepon tidak ditemukan");
            return;
        } else {
            System.out.println("Nomor telepon tidak valid");
            return;
        }
        
        
    }
}
