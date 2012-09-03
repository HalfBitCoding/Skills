package com.herocraftonline.heroes.characters.skill.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Setting;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillIdentify extends TargettedSkill
{
  public SkillIdentify(Heroes plugin)
  {
    super(plugin, "Identify");
    setDescription("Gives you info on the targeted player.");
    setUsage("/skill identify");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill identify" });

    setTypes(new SkillType[] { SkillType.KNOWLEDGE });
  }

  public String getDescription(Hero hero)
  {
    String description = getDescription();

    int cooldown = (SkillConfigManager.getUseSetting(hero, this, Setting.COOLDOWN.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, Setting.COOLDOWN_REDUCE.node(), 0, false) * hero.getSkillLevel(this)) / 1000;

    if (cooldown > 0) {
      description = description + " CD:" + cooldown + "s";
    }

    int mana = SkillConfigManager.getUseSetting(hero, this, Setting.MANA.node(), 10, false) - SkillConfigManager.getUseSetting(hero, this, Setting.MANA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);

    if (mana > 0) {
      description = description + " M:" + mana;
    }

    int healthCost = SkillConfigManager.getUseSetting(hero, this, Setting.HEALTH_COST, 0, false) - SkillConfigManager.getUseSetting(hero, this, Setting.HEALTH_COST_REDUCE, mana, true) * hero.getSkillLevel(this);

    if (healthCost > 0) {
      description = description + " HP:" + healthCost;
    }

    int staminaCost = SkillConfigManager.getUseSetting(hero, this, Setting.STAMINA.node(), 0, false) - SkillConfigManager.getUseSetting(hero, this, Setting.STAMINA_REDUCE.node(), 0, false) * hero.getSkillLevel(this);

    if (staminaCost > 0) {
      description = description + " FP:" + staminaCost;
    }

    int delay = SkillConfigManager.getUseSetting(hero, this, Setting.DELAY.node(), 0, false) / 1000;
    if (delay > 0) {
      description = description + " W:" + delay + "s";
    }

    int exp = SkillConfigManager.getUseSetting(hero, this, Setting.EXP.node(), 0, false);
    if (exp > 0) {
      description = description + " XP:" + exp;
    }
    return description;
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.MAX_DISTANCE.node(), Integer.valueOf(10));
    node.set(Setting.MAX_DISTANCE_INCREASE.node(), Integer.valueOf(0));
    return node;
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();
    if (!(target instanceof Player)) {
      return SkillResult.INVALID_TARGET;
    }
    if (((Player)target).equals(player)) {
      return SkillResult.INVALID_TARGET;
    }
    Player tPlayer = (Player)target;
    if (!damageCheck(player, tPlayer)) {
      Messaging.send(player, "You can't get info of that target", new Object[0]);
      return SkillResult.INVALID_TARGET_NO_MSG;
    }
    Hero tHero = this.plugin.getCharacterManager().getHero(tPlayer);
    broadcastExecuteText(hero, target);
    Messaging.send(player, "===============", new Object[0]);
    Messaging.send(player, "Name: $1", new Object[] { tHero.getPlayer().getDisplayName() });
    Messaging.send(player, "$1: $2", new Object[] { tHero.getHeroClass().getName(), Integer.valueOf(tHero.getTieredLevel(tHero.getHeroClass())) });
    Messaging.send(player, "$1: $2", new Object[] { tHero.getSecondClass().getName(), Integer.valueOf(tHero.getTieredLevel(tHero.getSecondClass())) });
    Messaging.send(player, "Health: $1", new Object[] { Integer.valueOf(tHero.getHealth()) });
    Messaging.send(player, "Mana: $1", new Object[] { Integer.valueOf(tHero.getMana()) });
    Messaging.send(player, "Debuffs: $1", new Object[] { tHero.getEffects() });
    Messaging.send(player, "===============", new Object[0]);

    return SkillResult.NORMAL;
  }
}