/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.transfer.context;

import com.google.common.base.Preconditions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryWrappers;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryWrapper;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class PlayerEntityContainerItemContext implements ContainerItemContext {
	private final ItemKey boundKey;
	private final Storage<ItemKey> slot;
	private final PlayerInventoryWrapper wrapper;

	public static ContainerItemContext ofHand(PlayerEntity player, Hand hand) {
		PlayerInventoryWrapper wrapper = InventoryWrappers.ofPlayerInventory(player.inventory);
		int slot = hand == Hand.MAIN_HAND ? player.inventory.selectedSlot : 40;
		return new PlayerEntityContainerItemContext(
				ItemKey.of(player.inventory.getStack(slot)), wrapper.slotWrapper(slot), wrapper);
	}

	public static ContainerItemContext ofCursor(PlayerEntity player) {
		PlayerInventoryWrapper wrapper = InventoryWrappers.ofPlayerInventory(player.inventory);
		return new PlayerEntityContainerItemContext(
				ItemKey.of(player.inventory.getCursorStack()), wrapper.cursorSlotWrapper(), wrapper);
	}

	private PlayerEntityContainerItemContext(ItemKey boundKey, Storage<ItemKey> slot, PlayerInventoryWrapper wrapper) {
		this.boundKey = boundKey;
		this.slot = slot;
		this.wrapper = wrapper;
	}

	@Override
	public long getCount(Transaction tx) {
		long[] count = new long[] {0};
		slot.forEach(view -> {
			if (view.resource().equals(boundKey)) {
				count[0] = view.amount();
			}

			return true;
		}, tx);
		return count[0];
	}

	@Override
	public boolean transform(long count, ItemKey into, Transaction tx) {
		Preconditions.checkArgument(count <= getCount(tx), "Can't transform items that are not available.");

		if (slot.extract(boundKey, count, tx) != count) {
			throw new AssertionError("Implementation error.");
		}

		if (!into.isEmpty() && internalInsert(into, count, tx) != count) {
			throw new AssertionError("Implementation error.");
		}

		return true;
	}

	private long internalInsert(ItemKey into, long count, Transaction tx) {
		long initialCount = count;
		count -= slot.insert(into, count, tx);
		count -= wrapper.offerOrDrop(into, count, tx);
		return initialCount - count;
	}
}