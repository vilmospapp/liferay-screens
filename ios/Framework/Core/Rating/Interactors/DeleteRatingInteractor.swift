/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
import UIKit

public class DeleteRatingInteractor: ServerWriteConnectorInteractor {
	
	let className: String
	let classPK: Int64
	let ratingsGroupCount: Int32

	var resultRating: RatingEntry?

	init(screenlet: BaseScreenlet?,
			className: String,
			classPK: Int64,
			ratingsGroupCount: Int32) {
		self.className = className
		self.classPK = classPK
		self.ratingsGroupCount = ratingsGroupCount

		super.init(screenlet: screenlet)
	}
	
	override public func createConnector() -> ServerConnector? {
		return LiferayServerContext.connectorFactory.createRatingDeleteConnector(
			classPK: classPK,
			className: className,
			ratingsGroupCount: ratingsGroupCount)
	}
	
	override public func completedConnector(c: ServerConnector) {
		if let deleteCon = c as? RatingDeleteLiferayConnector {
			self.resultRating = deleteCon.resultRating
		}
	}
	
}
