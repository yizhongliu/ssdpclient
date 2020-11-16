package pri.tool.ssdpclient;

import static pri.tool.ssdpclient.SSDPConstants.HOST;
import static pri.tool.ssdpclient.SSDPConstants.MAN;
import static pri.tool.ssdpclient.SSDPConstants.NEWLINE;
import static pri.tool.ssdpclient.SSDPConstants.SL_M_SEARCH;

/**
 * Msg的实体类，格式详见toString()
 */
public class SSDPSearchMsg {
    private int mMX = 5; /* seconds to delay response */
    private String mST; /* Search target */

    public SSDPSearchMsg(String ST) {
        mST = ST;
    }

    public int getmMX() {
        return mMX;
    }

    public void setmMX(int mMX) {
        this.mMX = mMX;
    }

    public String getmST() {
        return mST;
    }

    public void setmST(String mST) {
        this.mST = mST;
    }

    /**
     * @ruturn 发送格式：
     * M-SEARCH * HTTP/1.1
     * Host:239.255.255.250:1900
     * Man:"ssdp:discover"
     * MX:5
     * ST:miivii
     */
    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        content.append(SL_M_SEARCH).append(NEWLINE);
        content.append(HOST).append(NEWLINE);
        content.append(MAN).append(NEWLINE);
        content.append("MX:" + mMX).append(NEWLINE);
        content.append(mST).append(NEWLINE);
        content.append(NEWLINE);
        return content.toString();
    }
}

