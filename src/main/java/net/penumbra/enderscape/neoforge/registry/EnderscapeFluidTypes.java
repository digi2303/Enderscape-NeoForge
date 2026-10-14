package net.penumbra.enderscape.neoforge.registry;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.penumbra.enderscape.Enderscape;
import net.penumbra.enderscape.neoforge.block.fluid.VoidLachrymaFluidType;

public class EnderscapeFluidTypes {

    public static final FluidType VOID_LACHRYMA = Registry.register(NeoForgeRegistries.FLUID_TYPES, Enderscape.id("void_lachryma"), new VoidLachrymaFluidType());
}
