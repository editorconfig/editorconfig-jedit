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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.util.Log;

public class EditorConfigPlugin extends EditPlugin implements EBComponent
{
    static private EditorConfigPlugin plugin;

    // path to EditorConfig Executable
    private String editorConfigExecutablePath = "editorconfig";

    // get the plugin instance
    static public EditorConfigPlugin getPlugin()
    {
        return plugin;
    }

    public EditorConfigPlugin()
    {
        plugin = this;
    }

    public void setEditorConfigExecutablePath(String exec)
    {
        editorConfigExecutablePath = exec;
    }

    public String getEditorConfigExecutablePath()
    {
        return editorConfigExecutablePath;
    }

    @Override
    public void start()
    {
        EditBus.addToBus(this);
    }

    @Override
    public void stop()
    {
        EditBus.removeFromBus(this);
    }

    public void loadEditorConfig(Buffer buf)
        throws IOException, NumberFormatException
    {
        Process proc;
        proc = new ProcessBuilder(editorConfigExecutablePath,
                buf.getPath()).start();

        InputStreamReader isr = new InputStreamReader(
                proc.getInputStream());
        BufferedReader br = new BufferedReader(isr);

        String line;
        while (true)
        {
            if ((line = br.readLine()) == null)
                break;

            // Get the position of '='
            int eq_pos = line.indexOf('=');

            if (eq_pos == -1 || // = is not found, skip this line
                    eq_pos == 0 || // Left side of = is empty
                    eq_pos == line.length() - 1) // right side is empty
                continue;

            String key = line.substring(0, eq_pos).trim();
            String value = line.substring(eq_pos + 1).trim();

            if (key.equals("indent_style")) // soft or hard tabs?
            {
                if (value.equals("tab"))
                    buf.setBooleanProperty("noTabs", false);
                else if (value.equals("space"))
                    buf.setBooleanProperty("noTabs", true);
            }
            else if (key.equals("tab_width")) // the width of tab
            {
                int tab_width = 0;

                tab_width = Integer.parseInt(value);

                if (tab_width > 0)
                    buf.setIntegerProperty("tabSize", tab_width);
            }
            else if (key.equals("indent_size")) // the size of indent
            {
                int indent_size = 0;

                indent_size = Integer.parseInt(value);

                if (indent_size > 0)
                    buf.setIntegerProperty("indentSize", indent_size);
            }
            else if (key.equals("end_of_line")) // eof
            {
                if (value.equals("lf"))
                    buf.setStringProperty(JEditBuffer.LINESEP, "\n");
                else if (value.equals("crlf"))
                    buf.setStringProperty(JEditBuffer.LINESEP, "\r\n");
                else if (value.equals("cr"))
                    buf.setStringProperty(JEditBuffer.LINESEP, "\r");
            }
        }
    }
	public void handleMessage(EBMessage msg)
	{
		if (msg instanceof BufferUpdate)
		{
		    BufferUpdate bu_msg = (BufferUpdate) msg;
            Buffer buf = bu_msg.getBuffer();

		    if (bu_msg.getWhat() == BufferUpdate.LOADED)
            {
                try
                {
                    loadEditorConfig(buf);
                }
                catch(IOException e)
                {
                    Log.log(Log.ERROR, this,
                            "Failed to load EditorConfig: " + e.toString());
                    e.printStackTrace();
                }
                catch(NumberFormatException e)
                {
                    Log.log(Log.ERROR, this,
                            "Failed to load EditorConfig: " + e.toString());
                    e.printStackTrace();
                }
            }
		}
    }
}
