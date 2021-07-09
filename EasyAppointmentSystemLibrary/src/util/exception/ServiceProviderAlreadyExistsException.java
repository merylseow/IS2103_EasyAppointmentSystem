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
public class ServiceProviderAlreadyExistsException extends Exception {

    /**
     * Creates a new instance of <code>AdminNotFoundException</code> without
     * detail message.
     */
    public ServiceProviderAlreadyExistsException() {
    }

    /**
     * Constructs an instance of <code>AdminNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ServiceProviderAlreadyExistsException(String msg) {
        super(msg);
    }
}
