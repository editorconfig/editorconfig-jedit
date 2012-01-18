// Copyright (c) 2012 EditorConfig Team
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
//

package org.editorconfig.jedit;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

// The settings dialog
public class SettingsDialog extends JDialog implements ActionListener
{
    JTextField editorConfigPath_TextField;
    JButton editorConfigPathChoosingFile_Button;
    JButton ok_Button;
    JButton cancel_Button;

    public SettingsDialog()
    {
        this(null);
    }

    public SettingsDialog(Dialog owner)
    {
        // a model dialog
        super(owner, "EditorConfig Settings", true);

        setSize(160, 160);

        JPanel fileChoosingPanel = new JPanel();
        fileChoosingPanel.setLayout(
                new BoxLayout(fileChoosingPanel, BoxLayout.Y_AXIS));

        fileChoosingPanel.add(
                new JLabel("Path to EditorConfig core executable:"));

        JPanel fileChoosingBottomPanel = new JPanel();
        fileChoosingBottomPanel.setLayout(
                new BoxLayout(fileChoosingBottomPanel, BoxLayout.X_AXIS));

        editorConfigPath_TextField = new JTextField();
        editorConfigPath_TextField.setText(
                EditorConfigPlugin.getPlugin().getEditorConfigExecutablePath());
        fileChoosingBottomPanel.add(editorConfigPath_TextField);

        editorConfigPathChoosingFile_Button = new JButton("...");
        editorConfigPathChoosingFile_Button.setMaximumSize(
                new Dimension(
                    editorConfigPathChoosingFile_Button.getPreferredSize().width
                    ,
                    Integer.MAX_VALUE));
        editorConfigPathChoosingFile_Button.addActionListener(this);
        fileChoosingBottomPanel.add(editorConfigPathChoosingFile_Button);

        fileChoosingPanel.add(fileChoosingBottomPanel);

        getContentPane().add(fileChoosingPanel);

        JPanel okcancelPanel = new JPanel();

        ok_Button = new JButton("OK");
        ok_Button.addActionListener(this);
        okcancelPanel.add(ok_Button);

        cancel_Button = new JButton("Cancel");
        cancel_Button.addActionListener(this);
        okcancelPanel.add(cancel_Button);

        getContentPane().add(okcancelPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();

        setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == editorConfigPathChoosingFile_Button)
        {
            JFileChooser fc = new JFileChooser();

            // Only display files that can be executed
            fc.setFileFilter(new FileFilter()
                    {
                        public boolean accept(File f)
                        {
                            return f.canExecute();
                        }

                        public String getDescription()
                        {
                            return "Executables";
                        }
                    });
            // User does not choose OK
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            // Put the path to the editorConfigPath_TextField
            editorConfigPath_TextField.setText(fc.getSelectedFile().getPath());
        }
        else if (event.getSource() == cancel_Button)
        {
            dispose();
        }
        else if (event.getSource() == ok_Button)
        {
            EditorConfigPlugin.getPlugin().setEditorConfigExecutablePath(
                    editorConfigPath_TextField.getText());

            dispose();
        }
    }
}
