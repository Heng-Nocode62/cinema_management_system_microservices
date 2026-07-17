package com.heng.cms.paymentservice.gateway;

import com.heng.cms.paymentservice.dto.KhqrResult;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

@UtilityClass
public class KhqrBuilder {
    private static final String BAKONG_GUID = "dev.bakong.com.kh";
    private static final String CURRENCY_USD = "840";
    private static final String CURRENCY_KHR = "116";

    public KhqrResult buildKhqrResult(
            String merchantAccountId,
            String merchantName,
            String merchantCity,
            double amount,
            String currency,
            String billNumber,
            String storeLabel,
            String terminalLabel
    ) {

        String currencyCode = "KHR".equalsIgnoreCase(currency) ? CURRENCY_KHR : CURRENCY_USD;

        //29: merchant account information
        String mai29 = tvl("00",BAKONG_GUID) + tvl("01",merchantAccountId);

        //62: additional data field template
        String adf62 = tvl("01",truncate(billNumber,25))
                + tvl("07", truncate(terminalLabel,25))
                + tvl("08", truncate(storeLabel,25));
        // assemble body (every thing before crc)
        String body = tvl("00","01") // payload format indicator
                + tvl("01","12") // dynamic qr
                + tvl("29",mai29) //bakong merchant account
                + tvl("52","5999") // MCC
                + tvl("53",currencyCode) // currency
                + tvl("54",formatAmount(amount)) // amount
                + tvl("58","KH") //country
                + tvl("59",truncate(merchantName,25)) //merchant name
                + tvl("60",truncate(merchantCity,25)) // merchant city
                + tvl("62",adf62); // additional data
        String withCrcTag = body +"6304";
        String crc = String.format("%04X",crc16(withCrcTag));
        String khqrString = withCrcTag + crc;
        String md5 = DigestUtils.md5Hex(khqrString);
        return new KhqrResult(khqrString,md5);

    }


    private String tvl(String tag,String value){
        return tag + String.format(("%02d"), value.length()) + value;
    }

    private String truncate(String s, int max){
        if (s==null) return "";
        return s.length()<max? s: s.substring(0, max);
    }
    private String formatAmount(double amount){
        if (amount==Math.floor(amount)) return String.valueOf((long)amount);
        return String.format("%.2f", amount).replaceAll("0+$","").replaceAll("\\.$","");

    }

    private int crc16(String data){
        int crc = 0xFFFF;
        for (char c : data.toCharArray()){
            crc ^= (c<<8);
            for (int i=0; i<8; i++){
                crc = (crc & 0x8000) !=0 ? (crc<<1)^ 0x1021 : crc<<1;
            }
        }
        return crc&0xFFFF;
    }


}
