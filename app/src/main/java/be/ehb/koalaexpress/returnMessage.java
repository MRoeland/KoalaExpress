package be.ehb.koalaexpress;


import com.fasterxml.jackson.core.JsonProcessingException;

public class returnMessage {
    public resultHeader m_header;
    public String m_Content;

    public returnMessage() {
        m_header = new resultHeader();
    }

    public String getJSONmsg() throws JsonProcessingException {
        String headerToJson = JSONhelper.getDefaultObjectMapper().writeValueAsString(m_header);
        String retMsg;
        retMsg = headerToJson + "|";
        retMsg += m_Content;
        return retMsg;
    }

    public void setSucces(boolean succ, String msg) {
        m_header.m_Succes = succ;
        m_header.m_Message = msg;
    }
    public Boolean isSucces() {
        return m_header.m_Succes;
    }
    public String getErrorMessage() {
        return m_header.m_Message;
    }
    public String getHeaderMessage() {
        return m_header.m_Message;
    }
}
