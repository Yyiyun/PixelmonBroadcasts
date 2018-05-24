// Listens for Pokémon hatching from eggs.
package rs.expand.pixelmonbroadcasts.listeners;

// Remote imports.
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// Local imports.
import static rs.expand.pixelmonbroadcasts.PixelmonBroadcasts.*;
import static rs.expand.pixelmonbroadcasts.utilities.PrintingMethods.*;
import static rs.expand.pixelmonbroadcasts.utilities.PlaceholderMethods.*;

public class HatchListener
{
    @SubscribeEvent
    public void onHatchEvent(final EggHatchEvent event)
    {
        final String playerName = event.player.getName();
        final World world = event.player.getEntityWorld();
        final BlockPos location = event.player.getPosition();
        final EntityPixelmon pokemon = (EntityPixelmon) PixelmonEntityList.createEntityFromNBT(event.nbt, world);

        if (logHatches)
        {
            // Add "shiny" to our console message if we have a shiny legendary.
            String shinyAddition = "§7";
            if (pokemon.getIsShiny())
                shinyAddition = "§7shiny ";

            // Print a hatch message to console.
            printBasicMessage
            (
                    "§5PBR §f// §8Player §7" + playerName +
                    "§8's " + shinyAddition + event.nbt.getString(NbtKeys.NAME) +
                    "§8 egg hatched in world \"§7" + world.getWorldInfo().getWorldName() +
                    "§8\" at X:§7" + location.getX() +
                    "§8 Y:§7" + location.getY() +
                    "§8 Z:§7" + location.getZ()
            );
        }

        if (showShinyHatches && pokemon.getIsShiny())
        {
            // Parse placeholders and print!
            if (shinyHatchMessage != null)
            {
                // Set up our message. This is the same for all eligible players, so call it once and store it.
                // We use the normal hatch permission for shiny hatches, as per the config's explanation.
                final String finalMessage = replacePlaceholders(
                        shinyHatchMessage, playerName, true, false, pokemon, location);

                // Send off the message, the needed notifier permission and the flag to check.
                iterateAndSendEventMessage(finalMessage, pokemon, hoverShinyHatches, true,true,
                        "shinyhatch", "showShinyHatch");
            }
            else
                printBasicError("The shiny egg hatching message is broken, broadcast failed.");
        }
        else if (showHatches)
        {
            // Parse placeholders and print!
            if (hatchMessage != null)
            {
                // Set up our message. This is the same for all eligible players, so call it once and store it.
                final String finalMessage = replacePlaceholders(
                        hatchMessage, playerName, true, false, pokemon, location);

                // Send off the message, the needed notifier permission and the flag to check.
                // We use the basic hatch permission for shiny hatches, as per the config's explanation.
                iterateAndSendEventMessage(finalMessage, pokemon, hoverHatches, true, true,
                        "hatch", "showHatch");
            }
            else
                printBasicError("The egg hatching message is broken, broadcast failed.");
        }
    }
}

