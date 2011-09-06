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

import sopc2dts.lib.AvalonSystem;

public class GeneratorFactory {
	public static AbstractSopcGenerator createGeneratorFor(AvalonSystem sys, String type)
	{
		if(type.equalsIgnoreCase("dts"))
		{
			return new DTSGenerator(sys);
		} else if(type.equalsIgnoreCase("dtb"))
		{
			return new DTBGenerator(sys);
		} else if(type.equalsIgnoreCase("dtb-hex8"))
		{
			return new DTBHex8Generator(sys);
		} else if(type.equalsIgnoreCase("u-boot")) {
			return new UBootHeaderGenerator(sys);
		} else if(type.equalsIgnoreCase("kernel"))
		{
			return new KernelHeadersGenerator(sys);
		} else if(type.equalsIgnoreCase("kernel-full"))
		{
			return new SopcCreateHeaderFilesImitator(sys);
		}
		return null;
	}
}