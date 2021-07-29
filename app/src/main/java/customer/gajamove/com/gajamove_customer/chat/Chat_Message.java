package customer.gajamove.com.gajamove_customer.chat;


import java.io.Serializable;

import customer.gajamove.com.gajamove_customer.models.Customer;
import customer.gajamove.com.gajamove_customer.models.Member;

/**
 * Created by PC-GetRanked on 3/13/2018.
 */

public class Chat_Message implements Serializable
{
    private Customer customer;
    private Member member;
    private Message message;

    public Chat_Message() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
