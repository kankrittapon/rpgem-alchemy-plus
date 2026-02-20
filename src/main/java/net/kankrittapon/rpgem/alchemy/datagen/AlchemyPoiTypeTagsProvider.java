package net.kankrittapon.rpgem.alchemy.datagen;

import net.kankrittapon.rpgem.alchemy.RPGEMAlchemy;
import net.kankrittapon.rpgem.alchemy.init.ModAlchemyVillagers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider; // Use optimized provider base class if available, or TagsProvider
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AlchemyPoiTypeTagsProvider extends PoiTypeTagsProvider {
    public AlchemyPoiTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RPGEMAlchemy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).add(ModAlchemyVillagers.ALCHEMIST_POI.getKey());
    }
}
