package customer.gajamove.com.gajamove_customer.sos;


import customer.gajamove.com.gajamove_customer.models.EmailContact;
import customer.gajamove.com.gajamove_customer.models.PhoneContact;

/**
 * Created by Asim Shahzad on 12/15/2017.
 */
public interface ContactUpdateRequest {
    void onContactUpdateRequest(PhoneContact contact, int index);
    void onEmailUpdateRequest(EmailContact emailContact, int index);
    void onEmailDeleteRequest(EmailContact contact, int index);
    void onPhoneDeleteRequest(PhoneContact contact, int index);
}
