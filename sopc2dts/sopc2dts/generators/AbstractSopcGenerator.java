/*
sopc2dts - Devicetree generation for Altera systems

Copyright (C) 2011 Walter Goossens <waltergoossens@home.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package sopc2dts.generators;

import java.util.Vector;

import sopc2dts.Logger;
import sopc2dts.Logger.LogLevel;
import sopc2dts.lib.AvalonSystem;
import sopc2dts.lib.BoardInfo;
import sopc2dts.lib.components.BasicComponent;

public abstract class AbstractSopcGenerator {
	protected static String copyRightNotice = "/*\n" +
	" * Copyright (C) 2010-2011 Walter Goossens <waltergoossens@home.nl>.\n" +
	" *\n" +
	" * This program is free software; you can redistribute it and/or modify\n" +
	" * it under the terms of the GNU General Public License as published by\n" +
	" * the Free Software Foundation; either version 2 of the License, or\n" +
	" * (at your option) any later version.\n" +
	" *\n" +
	" * This program is distributed in the hope that it will be useful, but\n" +
	" * WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
	" * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, GOOD TITLE or\n" +
	" * NON INFRINGEMENT.  See the GNU General Public License for more\n" +
	" * details.\n" +
	" *\n" +
	" * You should have received a copy of the GNU General Public License\n" +
	" * along with this program; if not, write to the Free Software\n" +
	" * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.\n" +
	" *\n" +
	" */\n";

	AvalonSystem sys;
	boolean textOutput;

	public AbstractSopcGenerator(AvalonSystem s, boolean isText)
	{
		sys = s;
		textOutput = isText;
	}
	
	public static String indent(int level)
	{
		String res = "";
		while(level-->0)
		{
			res += "\t";
		}
		return res;
	}
	
	public static String definenify(String in)
	{
		return in.toUpperCase().replace("-", "_");
	}
	
	protected static String getSmallCopyRightNotice(String componentName)
	{
		return "/*\n"
			+ " * This " + componentName + " is generated by sopc2dts\n"
			+ " * Sopc2dts is written by Walter Goossens <waltergoossens@home.nl>\n"
			+ " * in cooperation with the nios2 community <Nios2-dev@sopc.et.ntust.edu.tw>\n"
			+ " */\n";
	}
	
	public boolean isTextOutput() {
		return textOutput;
	}

	public abstract String getTextOutput(BoardInfo bi);
	/*
	 * This function can be overridden by classes that only support binary
	 */
	public byte[] getBinaryOutput(BoardInfo bi)
	{
		return getTextOutput(bi).getBytes();
	}
	protected BasicComponent getPovComponent(BoardInfo bi)
	{
		BasicComponent povComp = sys.getComponentByName(bi.getPov());
		if(povComp == null)
		{
			if((bi.getPov() == null) || (bi.getPov().isEmpty()))
			{
				Logger.logln("No point of view specified. Trying to find one.", LogLevel.INFO);
			} else {
				Logger.logln("Point of view: '" + bi.getPov() + "' could not be found. Trying to find another one.", LogLevel.WARNING);
			}
			Vector<BasicComponent> vMasters = sys.getMasterComponents();
			if(vMasters.isEmpty())
			{
				Logger.logln("System appears to not contain any master components!", LogLevel.ERROR);
			} else {
				switch(bi.getPovType())
				{
				case CPU: {
					//Find a CPU
					for(BasicComponent comp : vMasters)
					{
						if(comp.getScd().getGroup().equals("cpu"))
						{
							Logger.logln("Found a cpu of type " + comp.getClassName() + " named " + comp.getInstanceName(), LogLevel.INFO);
							return comp;
						}
					}
				} break;
				case PCI: {
					//First do a strict run
					for(BasicComponent comp : vMasters)
					{
						if(comp.getScd().getGroup().toLowerCase().contains("pci"))
						{
							Logger.logln("Found a master of type " + comp.getClassName() + " named " + comp.getInstanceName(), LogLevel.INFO);
							return comp;
						}
					}
					//Then a weaker one
					for(BasicComponent comp : vMasters)
					{
						if(comp.getClassName().toLowerCase().contains("pci"))
						{
							Logger.logln("Found a master of type " + comp.getClassName() + " named " + comp.getInstanceName(), LogLevel.INFO);
							return comp;
						}
					}
					//Then just return anything remotely matching
					for(BasicComponent comp : vMasters)
					{
						if(comp.getInstanceName().toLowerCase().contains("pci"))
						{
							Logger.logln("Found a master of type " + comp.getClassName() + " named " + comp.getInstanceName(), LogLevel.WARNING);
							return comp;
						}
					}
				} break;
				}
				povComp = vMasters.firstElement();
				Logger.logln("Unable to find a master of type " + bi.getPovType().name() + ". Randomly selecting the first master we find (" + povComp.getInstanceName() + ").", LogLevel.WARNING);
			}
		}
		return povComp;
	}
}
