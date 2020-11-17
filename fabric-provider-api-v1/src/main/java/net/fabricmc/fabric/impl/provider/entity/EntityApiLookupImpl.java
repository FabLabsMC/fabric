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

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.provider.v1.ApiProviderMap;
import net.fabricmc.fabric.api.provider.v1.ContextKey;
import net.fabricmc.fabric.api.provider.v1.entity.EntityApiLookup;

final class EntityApiLookupImpl<T, C> implements EntityApiLookup<T, C> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ApiProviderMap<EntityType<?>, EntityApiProvider<?, T, C>> entityTypeProviderMap = ApiProviderMap.create();
	private final ApiProviderMap<Class<Entity>, EntityApiProvider<?, T, C>> entityClassProviderMap = ApiProviderMap.create();
	private final Identifier id;
	private final ContextKey<C> contextKey;

	EntityApiLookupImpl(Identifier apiId, ContextKey<C> contextKey) {
		this.id = apiId;
		this.contextKey = contextKey;
	}

	@Override
	public @Nullable T get(Entity entity, C context) {
		Objects.requireNonNull(entity, "entity cannot be null");
		// Providers have the final say whether a null context is allowed.

		// Entities have two access methods due to the way registration works.
		// Check the entity type first and fallback to entity class if we get no api.
		@SuppressWarnings("unchecked")
		final EntityApiProvider<Entity, T, C> providerFromType = (EntityApiProvider<Entity, T, C>) this.entityTypeProviderMap.get(entity.getType());

		if (providerFromType != null) {
			// See if the api returned is null
			final T api = providerFromType.get(entity, context);

			if (api != null) {
				return api;
			}
		}

		// Try the entity class
		//noinspection unchecked
		final EntityApiProvider<Entity, T, C> providerFromClass = (EntityApiProvider<Entity, T, C>) this.entityClassProviderMap.get((Class<Entity>) entity.getClass());

		if (providerFromClass != null) {
			return providerFromClass.get(entity, context);
		}

		return null;
	}

	@Override
	public <E extends Entity> void registerForEntityType(EntityApiProvider<E, T, C> provider, EntityType<E> entityType) {
		Objects.requireNonNull(provider, "EntityApiProvider cannot be null");
		Objects.requireNonNull(entityType, "Entity type cannot be null");

		throw new UnsupportedOperationException("Implement me!");
	}

	@Override
	public <E extends Entity> void registerForEntityClass(EntityApiProvider<E, T, C> provider, Class<E> type) {
		Objects.requireNonNull(provider, "EntityApiProvider cannot be null");
		Objects.requireNonNull(type, "Entity class cannot be null");

		throw new UnsupportedOperationException("Implement me!");
	}

	@Override
	public Identifier getApiId() {
		return this.id;
	}

	@Override
	public ContextKey<C> getContextKey() {
		return this.contextKey;
	}
}
