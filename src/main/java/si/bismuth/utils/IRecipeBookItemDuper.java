package si.bismuth.utils;

public interface IRecipeBookItemDuper {
	void bismuthServer$clearDupeItem();

	void bismuthServer$dupeItem(int slot);

	int bismuthServer$getDupeItem();

	void bismuthServer$dupeItemScan(boolean s);
}
