/*
 * @(#) IntegerVerifier.java version 1.0.0;       June 8 2009
 *
 * Copyright (c) 2002-2009 Blue Map (DIP Labs), Ltd. All Rights Reserved.
 *
 * Class for to ensure that the text entered by the user is valid
 *
 *=============================================================
 *  Last changes :
 *          24 November 2009 by Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *          File version 1.0.1
 *          Edit method - System comment
 */

/** Class for to ensure that the text entered by the user is valid
 *  @author Gregory S. Bikineev, Dmitry D. Moiseenko, Pavel V. Maksimov
 *  @version 1.0 - June 2009
 */

package util;

import java.awt.Toolkit;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class IntegerVerifier extends InputVerifier
{

    /**
     * @Override
     * This method controll that in JTextField was inputed valid symbol
     * @param input  - inputed symbol
     * @return - return true if inputed symbol is valid
     */

    public boolean verify(JComponent input)
    {
        System.out.println("IntegerVerifier: method: verify: start");

        try
        {
            JTextField textfield = (JTextField) input;
            Integer.parseInt(textfield.getText());
            return true;
        }
        catch (NumberFormatException e)
        {
            Toolkit.getDefaultToolkit().beep();
        }
        return false;
    }

}
