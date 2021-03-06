package joshie.harvestmoon.cooking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import joshie.harvestmoon.api.cooking.ICookingComponent;
import joshie.harvestmoon.api.cooking.IFoodRegistry;
import joshie.harvestmoon.api.cooking.IMeal;
import joshie.harvestmoon.api.cooking.IMealRecipe;
import joshie.harvestmoon.api.cooking.IUtensil;
import joshie.harvestmoon.core.helpers.SafeStackHelper;
import joshie.harvestmoon.core.util.SafeStack;
import joshie.harvestmoon.init.HMItems;
import joshie.harvestmoon.items.ItemMeal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class FoodRegistry implements IFoodRegistry {
    private static final Multimap<SafeStack, ICookingComponent> registry = ArrayListMultimap.create();
    private static final ArrayList<IMealRecipe> recipes = new ArrayList(250);
    private static final HashMap<String, ICookingComponent> components = new HashMap();
    private static final HashSet meals = new HashSet();

    @Override
    public void register(ItemStack stack, ICookingComponent component) {
        if (stack == null || stack.getItem() == null || component == null) return; //Fail silently
        FoodRegistry.registry.get(SafeStackHelper.getSafeStackType(stack)).add(component);

        //Register the component
        if (!components.containsKey(component.getUnlocalizedName())) {
            components.put(component.getUnlocalizedName(), component);
        }
    }

    @Override
    public List<ICookingComponent> getCookingComponents(ItemStack stack) {
        return (List<ICookingComponent>) SafeStackHelper.getResult(stack, registry);
    }

    @Override
    public ICookingComponent getComponent(String unlocalized) {
        return components.get(unlocalized);
    }

    @Override
    public Fluid getFluid(ItemStack ingredient) {
        return ((List<ICookingComponent>)SafeStackHelper.getResult(ingredient, registry)).get(0).getFluid();
    }

    @Override
    public IUtensil getUtensil(String unlocalized) {
        for (Utensil utensil : Utensil.values()) {
            if (utensil.name().equalsIgnoreCase(unlocalized)) return utensil;
        }

        return Utensil.KITCHEN;
    }

    @Override
    public void addRecipe(IMealRecipe recipe) {
        recipes.add(recipe);
        meals.add(recipe.getBestMeal()); //Add a meal to the list
    }

    @Override
    public ArrayList<IMealRecipe> getRecipes() {
        return recipes;
    }

    @Override
    public Set<IMeal> getMeals() {
        return meals;
    }

    @Override
    public ItemStack getResult(IUtensil utensil, ArrayList<ItemStack> ingredients) {
        //Convert all the stacks in to their relevant ingredients
        HashSet<ICookingComponent> components = new HashSet();
        for (ItemStack stack : ingredients) {
            components.addAll(getCookingComponents(stack));
        }

        for (IMealRecipe recipe : recipes) {
            IMeal meal = recipe.getMeal(utensil, components);
            if (meal != null) {
                return ItemMeal.cook(new ItemStack(HMItems.meal), meal);
            }
        }

        ItemStack burnt = Meal.BURNT.copy();
        burnt.setItemDamage(utensil.ordinal());
        return burnt;
    }
}
