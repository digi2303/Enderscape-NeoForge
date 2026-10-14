package net.penumbra.enderscape.neoforge.item;

import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.penumbra.enderscape.neoforge.EnderscapeNeoForge;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class EnderscapeCreativeModeTabOutput implements CreativeModeTab.Output {

    private final BuildCreativeModeTabContentsEvent event;

    private EnderscapeCreativeModeTabOutput(BuildCreativeModeTabContentsEvent event) {
        this.event = event;
    }

    public static void modify(ResourceKey<CreativeModeTab> tab, Consumer<EnderscapeCreativeModeTabOutput> modifier) {
        EnderscapeNeoForge.modEventBus().addListener(BuildCreativeModeTabContentsEvent.class, event -> {
            if (event.getTabKey().equals(tab)) {
                modifier.accept(new EnderscapeCreativeModeTabOutput(event));
            }
        });
    }

    public CreativeModeTab.ItemDisplayParameters getContext() {
        return event.getParameters();
    }

    @Override
    public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        insert(null, true, List.of(stack), visibility);
    }

    public void insertAfter(ItemLike anchor, ItemLike... items) {
        insertAfter(anchor, stacks(items));
    }

    public void insertAfter(ItemLike anchor, ItemStack... stacks) {
        insertAfter(anchor, Arrays.asList(stacks));
    }

    public void insertAfter(ItemLike anchor, Collection<ItemStack> stacks) {
        insert(anchor.asItem(), true, stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public void insertBefore(ItemLike anchor, ItemLike... items) {
        insertBefore(anchor, stacks(items));
    }

    public void insertBefore(ItemLike anchor, ItemStack... stacks) {
        insertBefore(anchor, Arrays.asList(stacks));
    }

    public void insertBefore(ItemLike anchor, Collection<ItemStack> stacks) {
        insert(anchor.asItem(), false, stacks, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static List<ItemStack> stacks(ItemLike... items) {
        return Arrays.stream(items).map(ItemStack::new).toList();
    }

    private void insert(@Nullable Item anchor, boolean after, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
        if (visibility != CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY) {
            insert(event.getParentEntries(), anchor, after, stacks, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        }

        if (visibility != CreativeModeTab.TabVisibility.PARENT_TAB_ONLY) {
            insert(event.getSearchEntries(), anchor, after, stacks, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        }
    }

    private void insert(ObjectSortedSet<ItemStack> entries, @Nullable Item anchor, boolean after, Collection<ItemStack> stacks, CreativeModeTab.TabVisibility visibility) {
        ItemStack target = null;

        if (anchor != null) {
            for (ItemStack entry : entries) {
                if (entry.is(anchor)) {
                    target = entry;
                    if (!after) break;
                }
            }
        }

        for (ItemStack stack : stacks) {
            if (entries.contains(stack)) continue;

            if (target == null) {
                event.accept(stack, visibility);
            } else if (after) {
                event.insertAfter(target, stack, visibility);
                target = stack;
            } else {
                event.insertBefore(target, stack, visibility);
            }
        }
    }
}
