package basemod.patches.com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.megacrit.cardcrawl.helpers.ImageMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;

import basemod.BaseMod;

@SpirePatch(cls="com.megacrit.cardcrawl.screens.charSelect.CharacterOption", method="ctor",
		paramtypes={"java.lang.String", "com.megacrit.cardcrawl.characters.AbstractPlayer$PlayerClass",
				"java.lang.String", "java.lang.String"})
public class CtorSwitch {
	public static final Logger logger = LogManager.getLogger(BaseMod.class.getName());
	
	@SpireInsertPatch(rloc=20)
	public static void Insert(Object __obj_instance, String optionName, Object cObj, String buttonUrl, String portraiImg) {
		CharacterOption option = (CharacterOption) __obj_instance;
		AbstractPlayer.PlayerClass chosenClass = (AbstractPlayer.PlayerClass) cObj;
		if (!BaseMod.isBaseGameCharacter(chosenClass)) {
			try {
				Field charInfoField;
				charInfoField = CharacterOption.class.getDeclaredField("charInfo");
				charInfoField.setAccessible(true);
				@SuppressWarnings("rawtypes")
				Class characterClass = BaseMod.getPlayerClass(chosenClass.toString());
				@SuppressWarnings("unchecked")
				Method getLoadout = characterClass.getMethod("getLoadout");
				charInfoField.set(option, getLoadout.invoke(null));
			} catch (NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				logger.error("could not create character loadout for " + chosenClass.toString());
				logger.error("with exception: " + e.getMessage());
				e.printStackTrace();
			}

			try {
				// fix texture loading
				Field buttonImgField;
				buttonImgField = CharacterOption.class.getDeclaredField("buttonImg");
				buttonImgField.setAccessible(true);
				buttonImgField.set(option, ImageMaster.loadImage(BaseMod.getPlayerButton(chosenClass.toString())));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.error("could not create character select button for " + chosenClass.toString());
				logger.error("with exception: " + e.getMessage());
				e.printStackTrace();
			}

		}
	}
}
