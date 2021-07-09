/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author jiajun
 */
public class ServiceProviderNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ServiceProviderNotFoundException</code>
     * without detail message.
     */
    public ServiceProviderNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ServiceProviderNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServiceProviderNotFoundException(String msg) {
        super(msg);
    }
}
