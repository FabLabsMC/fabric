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

package net.fabricmc.fabric.impl.provider.entity;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.provider.v1.ApiLookupMap;
import net.fabricmc.fabric.api.provider.v1.ContextKey;
import net.fabricmc.fabric.api.provider.v1.entity.EntityApiLookup;

public final class EntityApiLookupRegistryImpl {
	private static final ApiLookupMap<EntityApiLookupImpl<?, ?>> PROVIDERS = ApiLookupMap.create(EntityApiLookupImpl::new);

	public static <T, C> EntityApiLookup<T, C> getLookup(Identifier key, ContextKey<C> contextKey) {
		//noinspection unchecked
		return (EntityApiLookup<T, C>) PROVIDERS.getLookup(key, contextKey);
	}

	private EntityApiLookupRegistryImpl() {
	}
}
