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

package net.fabricmc.fabric.api.provider.v1.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.api.provider.v1.ApiLookup;

public interface EntityApiLookup<T, C> extends ApiLookup<C> {
	@Nullable
	T get(Entity entity, C context);

	<E extends Entity> void registerForEntityType(EntityApiProvider<E, T, C> provider, EntityType<E> entityType);

	<E extends Entity> void registerForEntityClass(EntityApiProvider<E, T, C> provider, Class<E> type);

	interface EntityApiProvider<E, T, C> {
		@Nullable
		T get(E entity, C context);
	}
}
